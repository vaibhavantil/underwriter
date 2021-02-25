package com.hedvig.underwriter.service.quoteStrategies

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.notificationService.quoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService

class DanishAccidentDataStrategy(productPricingService: ProductPricingService) : QuoteStrategy(productPricingService) {
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
