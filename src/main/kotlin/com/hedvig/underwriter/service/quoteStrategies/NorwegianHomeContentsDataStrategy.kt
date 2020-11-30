package com.hedvig.underwriter.service.quoteStrategies

import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.NorwegianHomeContentsGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianPersonGuidelines
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.notificationService.quoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import org.javamoney.moneta.Money

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
