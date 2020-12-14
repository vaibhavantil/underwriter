package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.model.StartSignResponse
import org.springframework.stereotype.Service

@Service
class SignStrategyService(
    val swedishBankIdSignStrategy: SwedishBankIdSignStrategy,
    val redirectSignStrategy: RedirectSignStrategy
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        val strategy = quotes.getStrategiesFromQuotes()

        if (strategy.isEmpty()) {
            throw RuntimeException("No strategy from quotes: $quotes")
        }

        if (strategy.size > 1) {
            throw RuntimeException("More than one strategy from quotes: $quotes")
        }

        return strategy.first().startSign(quotes, signData)
    }

    private fun List<Quote>.getStrategiesFromQuotes() = this.map {
        when (it.data) {
            is SwedishHouseData,
            is SwedishApartmentData -> swedishBankIdSignStrategy
            is NorwegianHomeContentsData,
            is NorwegianTravelData,
            is DanishHomeContentsData,
            is DanishAccidentData,
            is DanishTravelData -> redirectSignStrategy
        }
    }.toSet()
}
