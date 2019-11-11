package com.hedvig.underwriter.model

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.Address
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioQuoteRequestDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.Year
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

data class DatabaseQuoteRevision(
    val id: Int?,
    val masterQuoteId: UUID,
    val timestamp: Instant,
    val validity: Long,
    val productType: ProductType = ProductType.UNKNOWN,
    val state: QuoteState,
    val attributedTo: Partner,
    val currentInsurer: String? = "",
    val startDate: LocalDate? = null,
    val price: BigDecimal? = null,
    val quoteApartmentDataId: Int?,
    val quoteHouseDataId: Int?,
    val memberId: String?,
    val initiatedFrom: QuoteInitiatedFrom?,
    val createdAt: Instant?,
    val originatingProductId: UUID?,
    val signedProductId: UUID?
) {

    companion object {
        fun from(quote: Quote, id: Int? = null, timestamp: Instant = Instant.now()) =
            DatabaseQuoteRevision(
                id = id,
                masterQuoteId = quote.id,
                timestamp = timestamp,
                validity = quote.validity,
                productType = quote.productType,
                state = quote.state,
                attributedTo = quote.attributedTo,
                currentInsurer = quote.currentInsurer,
                startDate = quote.startDate,
                price = quote.price,
                quoteApartmentDataId = when (quote.data) {
                    is ApartmentData -> quote.data.internalId
                    else -> null
                },
                quoteHouseDataId = when (quote.data) {
                    is HouseData -> quote.data.internalId
                    else -> null
                },
                memberId = quote.memberId,
                createdAt = quote.createdAt,
                initiatedFrom = quote.initiatedFrom,
                originatingProductId = quote.originatingProductId,
                signedProductId = quote.signedProductId
            )
    }
}

const val ONE_DAY = 86_400L

data class Quote(
    val id: UUID,
    val createdAt: Instant,
    val price: BigDecimal? = null,
    val productType: ProductType = ProductType.UNKNOWN,
    val state: QuoteState,
    val initiatedFrom: QuoteInitiatedFrom,
    val attributedTo: Partner,
    val data: QuoteData,

    val currentInsurer: String? = null,

    val startDate: LocalDate? = null,

    val validity: Long = ONE_DAY * 30,
    val memberId: String? = null,
    val originatingProductId: UUID? = null,
    val signedProductId: UUID? = null
) {
    val isComplete: Boolean
        get() = when {
            price == null -> false
            productType == ProductType.UNKNOWN -> false
            !data.isComplete -> false
            else -> true
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
            productType = incompleteQuoteDto.productType ?: productType,
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
        if (
            incompleteQuoteDto.productType == ProductType.APARTMENT ||
            incompleteQuoteDto.incompleteApartmentQuoteData != null
        ) {
            val apartmentRequest = incompleteQuoteDto.incompleteApartmentQuoteData!!
            val newQuoteData: ApartmentData = when {
                newQuote.data is ApartmentData -> newQuote.data as ApartmentData
                newQuote.data is HouseData -> {
                    val houseData = newQuote.data as HouseData
                    ApartmentData(
                        id = houseData.id,
                        firstName = houseData.firstName,
                        lastName = houseData.lastName,
                        ssn = houseData.ssn,
                        street = houseData.street,
                        zipCode = houseData.zipCode,
                        city = houseData.city,
                        householdSize = houseData.householdSize,
                        livingSpace = houseData.livingSpace
                    )
                }
                else -> ApartmentData(id = UUID.randomUUID())
            }
            newQuote = newQuote.copy(
                data = newQuoteData.copy(
                    street = apartmentRequest.street ?: newQuoteData.street,
                    zipCode = apartmentRequest.zipCode ?: newQuoteData.zipCode,
                    city = apartmentRequest.city ?: newQuoteData.city,
                    householdSize = apartmentRequest.householdSize ?: newQuoteData.householdSize,
                    livingSpace = apartmentRequest.livingSpace ?: newQuoteData.livingSpace,
                    subType = apartmentRequest.subType ?: newQuoteData.subType
                )
            )
        }

        if (
            incompleteQuoteDto.productType == ProductType.HOUSE ||
            incompleteQuoteDto.incompleteHouseQuoteData != null
        ) {
            val houseRequest = incompleteQuoteDto.incompleteHouseQuoteData!!
            val newQuoteData: HouseData = when {
                newQuote.data is HouseData -> newQuote.data as HouseData
                newQuote.data is ApartmentData -> {
                    val apartmentData = newQuote.data as ApartmentData
                    HouseData(
                        id = apartmentData.id,
                        firstName = apartmentData.firstName,
                        lastName = apartmentData.lastName,
                        ssn = apartmentData.ssn,
                        street = apartmentData.street,
                        zipCode = apartmentData.zipCode,
                        city = apartmentData.city,
                        householdSize = apartmentData.householdSize,
                        livingSpace = apartmentData.livingSpace
                    )
                }
                else -> HouseData(id = UUID.randomUUID())
            }
            newQuote = newQuote.copy(
                data = newQuoteData.copy(
                    street = houseRequest.street ?: newQuoteData.street,
                    zipCode = houseRequest.zipCode ?: newQuoteData.zipCode,
                    city = houseRequest.city ?: newQuoteData.city,
                    householdSize = houseRequest.householdSize ?: newQuoteData.householdSize,
                    livingSpace = houseRequest.livingSpace ?: newQuoteData.livingSpace,
                    numberOfBathrooms = houseRequest.numberOfBathrooms ?: newQuoteData.numberOfBathrooms,
                    isSubleted = houseRequest.isSubleted ?: newQuoteData.isSubleted,
                    extraBuildings = houseRequest.extraBuildings?.map((ExtraBuilding)::from)
                        ?: newQuoteData.extraBuildings,
                    ancillaryArea = houseRequest.ancillaryArea ?: newQuoteData.ancillaryArea,
                    yearOfConstruction = houseRequest.yearOfConstruction ?: newQuoteData.yearOfConstruction,
                    floor = houseRequest.floor
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

        // TODO add bypassing of underwriting guidelines
        if (errorStrings.isEmpty()) {
            return Right(
                this.copy(
                    price = getPriceRetrievedFromProductPricing(productPricingService),
                    state = QuoteState.QUOTED
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
                return HouseQuotePriceDto(
                    birthDate = completeQuoteData.ssn!!.birthDateFromSsn(),
                    livingSpace = completeQuoteData.livingSpace!!,
                    houseHoldSize = completeQuoteData.householdSize!!,
                    zipCode = completeQuoteData.zipCode!!,
                    ancillaryArea = completeQuoteData.ancillaryArea!!,
                    numberOfBathrooms = completeQuoteData.numberOfBathrooms!!,
                    yearOfConstruction = Year.of(completeQuoteData.yearOfConstruction!!),
                    extraBuildings = completeQuoteData.extraBuildings!!.map { extraBuilding -> ExtraBuildingDto(
                        id = null,
                        displayName = extraBuilding.displayName,
                        hasWaterConnected = extraBuilding.hasWaterConnected,
                        area = extraBuilding.area,
                        type = extraBuilding.type
                    ) },
                    isSubleted = completeQuoteData.isSubleted!!
                )
            }
            throw RuntimeException("missing data cannot create house quote price dto")
        }
    }
}
