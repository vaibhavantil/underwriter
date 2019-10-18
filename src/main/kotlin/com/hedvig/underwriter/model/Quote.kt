package com.hedvig.underwriter.model

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.Address
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioQuoteRequestDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun String.birthDateFromSsn(): LocalDate {
    val trimmedInput = this.trim().replace("-", "").replace(" ", "")
    return LocalDate.parse(
        trimmedInput.substring(0, 4) + "-" + trimmedInput.substring(
            4,
            6
        ) + "-" + trimmedInput.substring(6, 8)
    )
}

data class DatabaseQuote(
    val id: UUID,
    val createdAt: Instant,
    val quotedAt: Instant?,
    val signedAt: Instant?,
    val validity: Long,
    val productType: ProductType = ProductType.UNKNOWN,
    val initiatedFrom: QuoteInitiatedFrom,

    val currentInsurer: String? = "",

    val startDate: LocalDate? = null,

    val price: BigDecimal? = null,
    val quoteApartmentDataId: UUID?,
    val quoteHouseDataId: UUID?,
    val memberId: String?
) {

    companion object {
        fun from(quote: Quote) =
            DatabaseQuote(
                id = quote.id,
                quoteApartmentDataId = when (quote.data) {
                    is ApartmentData -> quote.data.id
                    else -> null
                },
                quoteHouseDataId = when (quote.data) {
                    is HouseData -> quote.data.id
                    else -> null
                },
                createdAt = quote.createdAt,
                quotedAt = quote.quotedAt,
                signedAt = quote.signedAt,
                validity = quote.validity,
                price = quote.price,
                currentInsurer = quote.currentInsurer,
                initiatedFrom = quote.initiatedFrom,
                productType = quote.productType,
                startDate = quote.startDate,
                memberId = quote.memberId
            )
    }
}

const val ONE_DAY = 86_400L

data class Quote(
    val id: UUID,
    val createdAt: Instant,
    val price: BigDecimal? = null,
    val productType: ProductType = ProductType.UNKNOWN,
    val initiatedFrom: QuoteInitiatedFrom,

    val data: QuoteData,

    val currentInsurer: String? = null,

    val startDate: LocalDate? = null,

    val quotedAt: Instant? = null,
    val signedAt: Instant? = null,
    val validity: Long = ONE_DAY * 30,
    val memberId: String? = null
) {
    val isComplete: Boolean
        get() = when {
            price == null -> false
            productType == ProductType.UNKNOWN -> false
            !data.isComplete -> false
            else -> true
        }
    val state: QuoteState
        get() = when (signedAt) {
            null -> when {
                createdAt.plusSeconds(validity).isBefore(Instant.now()) -> QuoteState.EXPIRED
                quotedAt != null -> QuoteState.QUOTED
                else -> QuoteState.INCOMPLETE
            }
            else -> QuoteState.SIGNED
        }

    private fun getPriceRetrievedFromProductPricing(productPricingService: ProductPricingService): BigDecimal {
        return when (this.data) {
            is ApartmentData -> productPricingService.priceFromProductPricingForApartmentQuote(homeQuotePriceDto(this)).price
            is HouseData -> productPricingService.priceFromProductPricingForHouseQuote(houseQuotePriceDto(this)).price
        }
    }

    fun getRapioQuoteRequestDto(email: String): RapioQuoteRequestDto {
        return when (val data = this.data) {
            is ApartmentData -> {
                RapioQuoteRequestDto(
                    this.price!!,
                    data.firstName!!,
                    data.lastName!!,
                    data.ssn!!.birthDateFromSsn(),
                    false,
                    Address(
                        data.street!!,
                        data.city!!,
                        data.zipCode!!,
                        0
                    ),
                    data.livingSpace!!.toFloat(),
                    data.subType!!,
                    currentInsurer,
                    data.householdSize!!,
                    startDate?.atStartOfDay(),
                    data.ssn,
                    email,
                    "",
                    initiatedFrom
                )
            }
            else -> throw RuntimeException("Incomplete quote is of unknown type: ${this.data::class}")
        }
    }

    fun update(incompleteQuoteDto: IncompleteQuoteDto): Quote {
        var newQuote = copy(
            data = when (data) {
                is ApartmentData -> data.copy(
                    ssn = incompleteQuoteDto.ssn ?: data.ssn,
                    firstName = incompleteQuoteDto.firstName ?: data.firstName,
                    lastName = incompleteQuoteDto.lastName ?: data.lastName,
                    subType = incompleteQuoteDto.incompleteApartmentQuoteData?.subType ?: data.subType
                )
                is HouseData -> data.copy(
                    ssn = incompleteQuoteDto.ssn ?: data.ssn,
                    firstName = incompleteQuoteDto.firstName ?: data.firstName,
                    lastName = incompleteQuoteDto.lastName ?: data.lastName
                )
            }
        )
        if (incompleteQuoteDto.incompleteApartmentQuoteData != null) {
            val apartmentData = incompleteQuoteDto.incompleteApartmentQuoteData
            val newQuoteData: ApartmentData = newQuote.data as ApartmentData
            newQuote = newQuote.copy(
                data = newQuoteData.copy(
                    street = apartmentData.street ?: newQuoteData.street,
                    zipCode = apartmentData.zipCode ?: newQuoteData.zipCode,
                    city = apartmentData.city ?: newQuoteData.city,
                    householdSize = apartmentData.householdSize ?: newQuoteData.householdSize,
                    livingSpace = apartmentData.livingSpace ?: newQuoteData.livingSpace,
                    subType = newQuoteData.subType // TODO
                )
            )
        }

        if (incompleteQuoteDto.incompleteHouseQuoteData != null) {
            val houseData = incompleteQuoteDto.incompleteHouseQuoteData
            val newQuoteData: HouseData = newQuote.data as HouseData
            newQuote = newQuote.copy(
                data = newQuoteData.copy(
                    street = houseData.street ?: newQuoteData.street,
                    zipCode = houseData.zipCode ?: newQuoteData.zipCode,
                    city = houseData.city ?: newQuoteData.city,
                    householdSize = houseData.householdSize ?: newQuoteData.householdSize,
                    livingSpace = houseData.livingSpace ?: newQuoteData.livingSpace
                )
            )
        }

        return newQuote
    }

    fun complete(
        debtChecker: DebtChecker,
        productPricingService: ProductPricingService
    ): Either<List<String>, Quote> {

        val quoteData = this.data
        val errorStrings = mutableListOf<String>()

        errorStrings.addAll(quoteData.passUwGuidelines())

        if (quoteData is PersonPolicyHolder<*>) {
            if (quoteData.age() < 18)
                errorStrings.add("member is younger than 18")
            if (!quoteData.ssnIsValid())
                errorStrings.add("invalid ssn")

            if (errorStrings.isEmpty()) {
                errorStrings.addAll(debtChecker.passesDebtCheck(quoteData))
            }
        }

        if (errorStrings.isEmpty()) {
            return Right(
                this.copy(
                    price = getPriceRetrievedFromProductPricing(productPricingService),
                    quotedAt = Instant.now()
                )
            )
        }

        return Left(errorStrings)
    }

    companion object {
        private fun homeQuotePriceDto(quote: Quote): ApartmentQuotePriceDto {
            val quoteData = quote.data
            if (quoteData is ApartmentData) {
                return ApartmentQuotePriceDto(
                    birthDate = quoteData.ssn!!.birthDateFromSsn(),
                    livingSpace = quoteData.livingSpace!!,
                    houseHoldSize = quoteData.householdSize!!,
                    zipCode = quoteData.zipCode!!,
                    houseType = quoteData.subType!!,
                    isStudent = quoteData.isStudent
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }

        private fun houseQuotePriceDto(completeQuote: Quote): HouseQuotePriceDto {
            val completeQuoteData = completeQuote.data
            if (completeQuoteData is HouseData) {
                //  TODO: complete
                return HouseQuotePriceDto(
                    birthDate = completeQuoteData.ssn!!.birthDateFromSsn()
                )
            }
            throw RuntimeException("missing data cannot create house quote price dto")
        }
    }
}
