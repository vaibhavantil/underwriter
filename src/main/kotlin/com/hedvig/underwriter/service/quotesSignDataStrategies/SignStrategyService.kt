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
        return when (quotes.size) {
            1 ->
                when (quotes[0].data) {
                    is SwedishApartmentData,
                    is SwedishHouseData -> swedishBankIdSignStrategy.startSign(quotes, signData)
                    is NorwegianHomeContentsData,
                    is NorwegianTravelData,
                    is DanishHomeContentsData -> redirectSignStrategy.startSign(quotes, signData)
                    is DanishAccidentData,
                    is DanishTravelData -> StartSignResponse.FailedToStartSign("", "")
                }
            2 -> {
                when {
                    SignUtil.areTwoValidNorwegianQuotes(quotes) ||
                        SignUtil.areTwoValidDanishQuotes(quotes) -> redirectSignStrategy.startSign(quotes, signData)
                    else -> {
                        StartSignResponse.FailedToStartSign("", "")
                    }
                }
            }
            3 -> when {
                SignUtil.areThreeValidDanishQuotes(quotes) -> redirectSignStrategy.startSign(quotes, signData)
                else -> {
                    StartSignResponse.FailedToStartSign("", "")
                }
            }
            else -> StartSignResponse.FailedToStartSign("", "")
        }
    }
}
