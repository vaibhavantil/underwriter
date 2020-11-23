package com.hedvig.underwriter.service.quoteStrategies

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import org.springframework.stereotype.Service

@Service
class StrategyService(
    private val debtChecker: DebtChecker,
    private val productPricingService: ProductPricingService
) {
    fun getStrategy(quote: Quote): QuoteStrategy = getStrategy(quote.data)

    fun getStrategy(quoteData: QuoteData): QuoteStrategy = when (quoteData) {
        is SwedishHouseData -> SwedishHouseDataStrategy(debtChecker, productPricingService)
        is SwedishApartmentData -> SwedishApartmentDataStrategy(debtChecker, productPricingService)
        is NorwegianHomeContentsData -> NorwegianHomeContentsDataStrategy(productPricingService)
        is NorwegianTravelData -> NorwegianTravelDataStrategy(productPricingService)
        is DanishHomeContentsData -> DanishHomeContentsDataStrategy()
        is DanishAccidentData -> DanishAccidentDataStrategy()
        is DanishTravelData -> DanishTravelDataStrategy()
    }
}
