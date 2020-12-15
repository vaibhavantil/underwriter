package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class SignStrategyService(
    private val swedishBankIdSignStrategy: SwedishBankIdSignStrategy,
    private val redirectSignStrategy: RedirectSignStrategy,
    private val simpleSignStrategy: SimpleSignStrategy,
    private val env: Environment
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        val strategy = quotes.getStrategiesFromQuotes(signData.enableSimpleSign)

        if (strategy.isEmpty()) {
            return StartSignErrors.noQuotes
        }

        if (strategy.size > 1) {
            return StartSignErrors.quotesCanNotBeBundled
        }

        val bundleErrorOrNull = when {
            quotes.areSwedishQuotes() -> {
                if (quotes.size > 1) {
                    SwedishBankIdSignStrategy.logger.error("Can not start signing swedish quotes [Quotes: $quotes]")
                    StartSignErrors.quotesCanNotBeBundled
                } else {
                    null
                }
            }
            quotes.areNorwegianQuotes() -> {
                if (quotes.size > 1 && !quotes.areTwoValidNorwegianQuotes()) {
                    RedirectSignStrategy.logger.error("Norwegian quotes is not valid [Quotes: $quotes]")
                    StartSignErrors.quotesCanNotBeBundled
                } else {
                    null
                }
            }
            quotes.areDanishQuotes() -> {
                when {
                    quotes.size == 1 -> {
                        if (quotes[0].data !is DanishHomeContentsData) {
                            RedirectSignStrategy.logger.error("Single danish quote can not be signed alone [Quotes: $quotes]")
                            StartSignErrors.singleQuoteCanNotBeSignedAlone
                        } else {
                            null
                        }
                    }
                    quotes.size > 1 -> {
                        if (!quotes.isValidDanishQuoteBundle()) {
                            RedirectSignStrategy.logger.error("Danish quotes is not valid [Quotes: $quotes]")
                            StartSignErrors.quotesCanNotBeBundled
                        } else {
                            null
                        }
                    }
                    else -> null
                }
            }
            else -> StartSignErrors.quotesCanNotBeBundled
        }

        if (bundleErrorOrNull != null) {
            return bundleErrorOrNull
        }

        return strategy.first().startSign(quotes, signData)
    }

    private fun List<Quote>.getStrategiesFromQuotes(enableSimpleSign: Boolean) = this.map {
        when (it.data) {
            is SwedishHouseData,
            is SwedishApartmentData -> swedishBankIdSignStrategy
            is NorwegianHomeContentsData,
            is NorwegianTravelData -> if (enableSimpleSign && env.activeProfiles.contains("staging")) {
                simpleSignStrategy
            } else {
                redirectSignStrategy
            }
            is DanishHomeContentsData,
            is DanishAccidentData,
            is DanishTravelData -> redirectSignStrategy
        }
    }.toSet()

    private fun List<Quote>.areSwedishQuotes() =
        this.all { it.data is SwedishApartmentData || it.data is SwedishHouseData }

    private fun List<Quote>.areTwoValidNorwegianQuotes(): Boolean =
        this.size == 2 &&
            this.any { it.data is NorwegianHomeContentsData } &&
            this.any { it.data is NorwegianTravelData }

    private fun List<Quote>.isValidDanishQuoteBundle(): Boolean =
        this.areTwoValidDanishQuotes() || this.areThreeValidDanishQuotes()

    private fun List<Quote>.areTwoValidDanishQuotes(): Boolean =
        this.size == 2 &&
            (this.any { it.data is DanishHomeContentsData } &&
                (this.any { it.data is DanishAccidentData } || this.any { it.data is DanishTravelData }))

    private fun List<Quote>.areThreeValidDanishQuotes(): Boolean =
        this.size == 3 &&
            this.any { it.data is DanishHomeContentsData } &&
            this.any { it.data is DanishAccidentData } &&
            this.any { it.data is DanishTravelData }
}

fun List<Quote>.areNorwegianQuotes(): Boolean {
    return this.all { quote ->
        quote.data is NorwegianHomeContentsData ||
            quote.data is NorwegianTravelData
    }
}

fun List<Quote>.areDanishQuotes(): Boolean {
    return this.all { quote ->
        quote.data is DanishHomeContentsData ||
            quote.data is DanishAccidentData ||
            quote.data is DanishTravelData
    }
}
