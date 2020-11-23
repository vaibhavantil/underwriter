package com.hedvig.underwriter.service.quoteStrategies

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.NorwegianHomeContentsGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianPersonGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianTravelGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishApartmentGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishPersonalGuidelines
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.notificationService.quoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import org.javamoney.moneta.Money

class SwedishApartmentDataStrategy(
    private val debtChecker: DebtChecker,
    val productPricingService: ProductPricingService
) : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is SwedishApartmentData)
        return quoteCreatedEvent(quote, quote.data.street, quote.data.zipCode, quote.data.subType!!.toString())
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "SEK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishPersonalGuidelines(
            debtChecker
        ).setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishApartmentGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class NorwegianHomeContentsDataStrategy(val productPricingService: ProductPricingService) : QuoteStrategy() {

    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is NorwegianHomeContentsData)

        val insuranceType = if (quote.data.isYouth) {
            when (quote.data.type) {
                NorwegianHomeContentsType.RENT -> "YOUTH_RENT"
                NorwegianHomeContentsType.OWN -> "YOUTH_OWN"
            }
        } else {
            quote.data.type.name
        }

        return quoteCreatedEvent(quote, quote.data.street, quote.data.zipCode, insuranceType)
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "NOK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianPersonGuidelines.setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianHomeContentsGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class NorwegianTravelDataStrategy(val productPricingService: ProductPricingService) : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is NorwegianTravelData)

        return quoteCreatedEvent(
            quote, null, null,
            if (quote.data.isYouth) {
                "YOUTH"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "NOK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianPersonGuidelines.setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianTravelGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class DanishHomeContentsDataStrategy : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is DanishHomeContentsData)

        return quoteCreatedEvent(
            quote,
            quote.data.street,
            quote.data.zipCode,
            if (quote.data.isStudent) {
                "STUDENT"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return InsuranceCost(
            MonetaryAmountV2("9999.00", "DKK"),
            MonetaryAmountV2("0", "DKK"),
            MonetaryAmountV2("9999.00", "DKK"),
            null
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }
}

class DanishAccidentDataStrategy : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is DanishAccidentData)

        return quoteCreatedEvent(
            quote,
            quote.data.street,
            quote.data.zipCode,
            if (quote.data.isStudent) {
                "STUDENT"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return InsuranceCost(
            MonetaryAmountV2("9999.00", "DKK"),
            MonetaryAmountV2("0", "DKK"),
            MonetaryAmountV2("9999.00", "DKK"),
            null
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }
}

class DanishTravelDataStrategy : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is DanishAccidentData)

        return quoteCreatedEvent(
            quote,
            quote.data.street,
            quote.data.zipCode,
            if (quote.data.isStudent) {
                "STUDENT"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return InsuranceCost(
            MonetaryAmountV2("9999.00", "DKK"),
            MonetaryAmountV2("0", "DKK"),
            MonetaryAmountV2("9999.00", "DKK"),
            null
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }
}
