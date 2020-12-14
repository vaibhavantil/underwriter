package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.StartRedirectBankIdSignResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.IllegalStateException

@Service
class RedirectSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        if (quotes.isEmpty()) {
            logger.error("No quotes on start sign RedirectSignStrategy")
            return StartSignErrors.noQuotes
        }
        when {
            quotes.areValidNorwegianQuotes() -> {
                if (quotes.size > 1 && !quotes.areTwoValidNorwegianQuotes()) {
                    logger.error("Norwegian quotes is not valid in RedirectSignStrategy [Quotes: $quotes]")
                    return StartSignErrors.quotesCanNotBeBundled
                }
            }
            quotes.areValidDanishQuotes() -> {
                if (quotes.size > 1 && (!quotes.areTwoValidDanishQuotes() || quotes.areThreeValidDanishQuotes())) {
                    logger.error("Danish quotes is not valid in RedirectSignStrategy [Quotes: $quotes]")
                    return StartSignErrors.quotesCanNotBeBundled
                }
            }
            else -> {
                logger.error("Quotes are not norwegian or danish in RedirectSignStrategy [Quotes: $quotes]")
                return StartSignErrors.quotesCanNotBeBundled
            }
        }

        if (signData.successUrl == null || signData.failUrl == null) {
            return StartSignErrors.targetURLNotProvided
        }

        val signSessionId = signSessionRepository.insert(quotes.map { it.id })

        val response = getRedirectBankIdSignResponse(quotes, signSessionId, signData.successUrl, signData.failUrl)

        return response.redirectUrl?.let { redirectUrl ->
            getStartSignResponse(quotes, redirectUrl)
        } ?: response.internalErrorMessage?.let {
            StartSignErrors.emptyRedirectUrlFromBankId(it)
        } ?: StartSignErrors.emptyRedirectUrlFromBankId(response.errorMessages!!.joinToString(", "))
    }

    private fun getRedirectBankIdSignResponse(
        quotes: List<Quote>,
        signSessionId: UUID,
        successUrl: String,
        failUrl: String
    ): StartRedirectBankIdSignResponse {
        return when {
            quotes.areValidNorwegianQuotes() -> memberService.startNorwegianBankIdSignQuotes(
                quotes.safelyGetMemberId(),
                signSessionId,
                quotes.safelyGetSsn(),
                successUrl,
                failUrl
            )
            quotes.areValidDanishQuotes() -> memberService.startDanishBankIdSignQuotes(
                quotes.safelyGetMemberId(),
                signSessionId,
                quotes.safelyGetSsn(),
                successUrl,
                failUrl
            )
            else -> throw IllegalStateException("quotes are not valid while getting the redirect response [Quotes: $quotes]")
        }
    }

    private fun getStartSignResponse(
        quotes: List<Quote>,
        redirectUrl: String
    ): StartSignResponse {
        return when {
            quotes.areValidNorwegianQuotes() -> StartSignResponse.NorwegianBankIdSession(redirectUrl)
            quotes.areValidDanishQuotes() -> StartSignResponse.DanishBankIdSession(redirectUrl)
            else -> throw IllegalStateException("quotes are not valid while getting the redirect response [Quotes: $quotes]")
        }
    }

    private fun List<Quote>.areValidNorwegianQuotes(): Boolean {
        return this.all { quote ->
            quote.data is NorwegianHomeContentsData ||
                quote.data is NorwegianTravelData
        }
    }

    private fun List<Quote>.areValidDanishQuotes(): Boolean {
        return this.all { quote ->
            quote.data is DanishHomeContentsData ||
                quote.data is DanishAccidentData ||
                quote.data is DanishTravelData
        }
    }

    private fun List<Quote>.areTwoValidNorwegianQuotes(): Boolean =
        this.size == 2 &&
            this.any { it.data is NorwegianHomeContentsData } &&
            this.any { it.data is NorwegianTravelData }

    private fun List<Quote>.areTwoValidDanishQuotes(): Boolean =
        this.size == 2 &&
            (this.any { it.data is DanishHomeContentsData } &&
                (this.any { it.data is DanishAccidentData } || this.any { it.data is DanishTravelData }))

    private fun List<Quote>.areThreeValidDanishQuotes(): Boolean =
        this.size == 3 &&
            this.any { it.data is DanishHomeContentsData } &&
            this.any { it.data is DanishAccidentData } &&
            this.any { it.data is DanishTravelData }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
