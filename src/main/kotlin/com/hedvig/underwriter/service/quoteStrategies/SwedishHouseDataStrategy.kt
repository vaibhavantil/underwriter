package com.hedvig.underwriter.service.quoteStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.SwedishHouseGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishPersonalGuidelines
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.notificationService.quoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService

class SwedishHouseDataStrategy(
    private val debtChecker: DebtChecker,
    productPricingService: ProductPricingService
) : QuoteStrategy(productPricingService) {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is SwedishHouseData)
        return quoteCreatedEvent(quote, quote.data.street, quote.data.zipCode, "HOUSE")
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishPersonalGuidelines(
            debtChecker
        ).setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishHouseGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}
