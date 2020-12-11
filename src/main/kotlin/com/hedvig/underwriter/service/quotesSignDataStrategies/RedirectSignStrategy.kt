package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.model.ssn
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.StartRedirectBankIdSignResponse
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.IllegalStateException

@Service
class RedirectSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        require(quotes.areValidNorwegianQuotes() || quotes.areValidDanishQuotes())

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
                quotes[0].memberId!!.toLong(),
                signSessionId,
                quotes[0].ssn,
                successUrl,
                failUrl
            )
            quotes.areValidDanishQuotes() -> memberService.startDanishBankIdSignQuotes(
                quotes[0].memberId!!.toLong(),
                signSessionId,
                quotes[0].ssn,
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
        return this.isNotEmpty() &&
            this.all { quote ->
                quote.data is NorwegianHomeContentsData ||
                    quote.data is NorwegianTravelData
            }
    }

    private fun List<Quote>.areValidDanishQuotes(): Boolean {
        return this.isNotEmpty() &&
            this.all { quote ->
                quote.data is DanishHomeContentsData ||
                    quote.data is DanishAccidentData ||
                    quote.data is DanishTravelData
            }
    }
}
