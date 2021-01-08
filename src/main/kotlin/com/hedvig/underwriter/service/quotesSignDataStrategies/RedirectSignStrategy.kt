package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.SignMethod
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.NationalIdentification
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.RedirectCountry
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterStartSignSessionResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Nationality
import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.UUID
import kotlin.IllegalStateException

@Service
class RedirectSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        if (signData.successUrl == null || signData.failUrl == null) {
            return StartSignErrors.targetURLNotProvided
        }

        val signMethod = when {
            quotes.areNorwegianQuotes() -> SignMethod.NORWEGIAN_BANK_ID
            quotes.areDanishQuotes() -> SignMethod.DANISH_BANK_ID
            else -> throw RuntimeException("Could not get sign method from quotes [$quotes]")
        }

        val signSessionId = signSessionRepository.insert(signMethod, quotes.map { it.id })

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
    ): UnderwriterStartSignSessionResponse.BankIdRedirect {
        return when {
            quotes.areNorwegianQuotes() -> memberService.startRedirectBankIdSign(
                quotes.safelyGetMemberId(),
                signSessionId,
                NationalIdentification(
                    quotes.safelyGetSsn(),
                    Nationality.NORWAY
                ),
                successUrl,
                failUrl,
                RedirectCountry.NORWAY
            )
            quotes.areDanishQuotes() -> memberService.startRedirectBankIdSign(
                quotes.safelyGetMemberId(),
                signSessionId,
                NationalIdentification(
                    quotes.safelyGetSsn(),
                    Nationality.DENMARK
                ),
                successUrl,
                failUrl,
                RedirectCountry.DENMARK
            )
            else -> throw IllegalStateException("quotes are not valid while getting the redirect response [Quotes: $quotes]")
        }
    }

    private fun getStartSignResponse(
        quotes: List<Quote>,
        redirectUrl: String
    ): StartSignResponse {
        return when {
            quotes.areNorwegianQuotes() -> StartSignResponse.NorwegianBankIdSession(redirectUrl)
            quotes.areDanishQuotes() -> StartSignResponse.DanishBankIdSession(redirectUrl)
            else -> throw IllegalStateException("quotes are not valid while getting the redirect response [Quotes: $quotes]")
        }
    }
}
