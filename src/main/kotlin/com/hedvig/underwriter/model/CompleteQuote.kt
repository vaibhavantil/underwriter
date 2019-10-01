package com.hedvig.underwriter.model

import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.UwGuidelinesChecker
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HomeQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@TypeDefs(
        TypeDef(name = "json", typeClass = JsonStringType::class),
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class))
class CompleteQuote (
        @field:Id
        @field:GeneratedValue(generator = "UUID")
        @field:GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        var id: UUID? = null,

        @OneToOne
        val incompleteQuote: IncompleteQuote,

        @Enumerated(EnumType.STRING)
        val quoteState: QuoteState = QuoteState.QUOTED,
        val quoteCreatedAt: Instant,

        @Enumerated(EnumType.STRING)
        val productType: ProductType = ProductType.UNKNOWN,

        @Enumerated(EnumType.STRING)
        val lineOfBusiness: LineOfBusiness,
        var price: BigDecimal?,

        @field:Type(type = "jsonb")
        @field:Column(columnDefinition = "jsonb")
        val completeQuoteData: CompleteQuoteData,

        @Enumerated(EnumType.STRING)
        val quoteInitiatedFrom: QuoteInitiatedFrom,

        val birthDate: LocalDate,
        val livingSpace: Int,
        val houseHoldSize: Int,
        val isStudent: Boolean,
        val ssn: String,

        @ElementCollection(targetClass=String::class)
        var reasonQuoteCannotBeCompleted: List<String> = listOf()
    ) {

    override fun hashCode(): Int {
        return 31
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            IncompleteQuote::class.isInstance(other) && this.id == (other as IncompleteQuote).id -> true
            else -> false
        }
    }

    fun passedUnderwritingGuidelines(uwGuidelinesChecker: UwGuidelinesChecker): Boolean {
        return if (this.completeQuoteData is CompleteQuoteData.Home) {
            uwGuidelinesChecker.meetsHomeUwGuidelines(this)
        } else {
            uwGuidelinesChecker.meetsHouseUwGuidelines(this)
        }
    }

    fun passedDebtCheck(debtChecker: DebtChecker): Boolean {
        return debtChecker.passesDebtCheck(this)
    }

    fun setPriceRetrievedFromProductPricing(productPricingService: ProductPricingService) {
        when {
            this.completeQuoteData is CompleteQuoteData.Home -> {
                this.price = productPricingService.priceFromProductPricingForHomeQuote(homeQuotePriceDto(this)).price
            }
            this.completeQuoteData is CompleteQuoteData.House ->  {
                this.price = productPricingService.priceFromProductPricingForHouseQuote(houseQuotePriceDto(this)).price
            }
            else -> throw RuntimeException("Cannot calculate price as incorrect product type - product type is ${this.productType}")
        }

    }
    companion object {
        private fun homeQuotePriceDto(completeQuote: CompleteQuote): HomeQuotePriceDto {
            if (completeQuote.completeQuoteData is CompleteQuoteData.Home) {
                return HomeQuotePriceDto(
                        birthDate = completeQuote.birthDate,
                        livingSpace = completeQuote.livingSpace,
                        houseHoldSize = completeQuote.houseHoldSize,
                        zipCode = completeQuote.completeQuoteData.zipCode,
                        floor = completeQuote.completeQuoteData.floor,
                        houseType = completeQuote.lineOfBusiness,
                        isStudent = completeQuote.isStudent
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }

        private fun houseQuotePriceDto(completeQuote: CompleteQuote): HouseQuotePriceDto {
            if (completeQuote.completeQuoteData is CompleteQuoteData.Home) {
                //  TODO: complete
                return HouseQuotePriceDto(
                        birthDate = completeQuote.birthDate
                )
            }
            throw RuntimeException("missing data cannot create house quote price dto")
        }
    }
}






