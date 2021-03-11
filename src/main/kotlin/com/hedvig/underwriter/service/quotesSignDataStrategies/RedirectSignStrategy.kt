package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Market
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

        val signSessionId = signSessionRepository.insert(quotes.map { it.id })

        val response = getRedirectBankIdSignResponse(quotes, signSessionId, signData.successUrl, signData.failUrl)

        return response.redirectUrl?.let { redirectUrl ->
            getStartSignResponse(quotes, redirectUrl)
        } ?: response.internalErrorMessage?.let {
            StartSignErrors.emptyRedirectUrlFromBankId(it)
        } ?: StartSignErrors.emptyRedirectUrlFromBankId(response.errorMessages!!.joinToString(", "))
    }

    override fun getSignMethod(quotes: List<Quote>): SignMethod = when (quotes.safelyMarket()) {
        Market.NORWAY -> SignMethod.NORWEGIAN_BANK_ID
        Market.DENMARK -> SignMethod.DANISH_BANK_ID
        else -> throw RuntimeException("quotes are not valid while getting the sign method [Quotes: $quotes]")
    }

    private fun getRedirectBankIdSignResponse(
        quotes: List<Quote>,
        signSessionId: UUID,
        successUrl: String,
        failUrl: String
    ): UnderwriterStartSignSessionResponse.BankIdRedirect {
        return when (quotes.safelyMarket()) {
            Market.NORWAY -> memberService.startRedirectBankIdSign(
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
            Market.DENMARK -> memberService.startRedirectBankIdSign(
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
        return when (quotes.safelyMarket()) {
            Market.NORWAY -> StartSignResponse.NorwegianBankIdSession(redirectUrl)
            Market.DENMARK -> StartSignResponse.DanishBankIdSession(redirectUrl)
            else -> throw IllegalStateException("quotes are not valid while getting the redirect response [Quotes: $quotes]")
        }
    }
}
