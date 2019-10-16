package com.hedvig.underwriter.model

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.Address
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HomeQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioQuoteRequestDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.dtos.PostIncompleteQuoteRequest
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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

data class Quote(
    val id: UUID,
    val state: QuoteState = QuoteState.QUOTED,
    val createdAt: Instant,
    val productType: ProductType = ProductType.UNKNOWN,
    val initiatedFrom: QuoteInitiatedFrom,

    val data: QuoteData,

    val currentInsurer: String? = "",

    val startDate: LocalDateTime? = null,

    val price: BigDecimal? = null
) {

    private fun getPriceRetrievedFromProductPricing(productPricingService: ProductPricingService): BigDecimal {
        return when (this.data) {
            is HomeData -> productPricingService.priceFromProductPricingForHomeQuote(homeQuotePriceDto(this)).price
            is HouseData -> productPricingService.priceFromProductPricingForHouseQuote(houseQuotePriceDto(this)).price
        }
    }

    fun getRapioQuoteRequestDto(email: String): RapioQuoteRequestDto {
        return when {
            this.data is HomeData -> {
                RapioQuoteRequestDto(
                    this.price!!,
                    this.data.firstName!!,
                    this.data.lastName!!,
                    this.data.ssn!!.birthDateFromSsn(),
                    false,
                    Address(
                        this.data.street!!,
                        this.data.city!!,
                        this.data.zipCode!!,
                        0
                    ),
                    this.data.livingSpace!!.toFloat(),
                    this.data.subType!!,
                    this.currentInsurer,
                    this.data.householdSize!!,
                    this.startDate,
                    this.data.ssn,
                    email,
                    ""
                )
            }
            else -> throw RuntimeException("Incomplete quote is of unknown type: ${this.data::class}")
        }
    }

    fun update(incompleteQuoteDto: PostIncompleteQuoteRequest) {
        // FIX update logic
    }

    fun complete(
        debtChecker: DebtChecker,
        productPricingService: ProductPricingService
    ): Either<MutableList<String>, Quote> {

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
            return Right(this.copy(price = getPriceRetrievedFromProductPricing(productPricingService), state = QuoteState.QUOTED))
        }

        return Left(errorStrings)
    }

    fun update(incompleteQuoteDto: IncompleteQuoteDto) {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private fun homeQuotePriceDto(quote: Quote): HomeQuotePriceDto {
            if (quote.data is HomeData) {
                return HomeQuotePriceDto(
                    birthDate = quote.data.ssn!!.birthDateFromSsn(),
                    livingSpace = quote.data.livingSpace!!,
                    houseHoldSize = quote.data.householdSize!!,
                    zipCode = quote.data.zipCode!!,
                    houseType = quote.data.subType!!,
                    isStudent = quote.data.isStudent
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }

        private fun houseQuotePriceDto(completeQuote: Quote): HouseQuotePriceDto {
            if (completeQuote.data is HouseData) {
                //  TODO: complete
                return HouseQuotePriceDto(
                    birthDate = completeQuote.data.ssn!!.birthDateFromSsn()
                )
            }
            throw RuntimeException("missing data cannot create house quote price dto")
        }
    }
}
