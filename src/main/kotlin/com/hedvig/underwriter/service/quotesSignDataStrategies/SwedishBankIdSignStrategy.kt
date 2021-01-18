package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.SignMethod
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.NationalIdentification
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Nationality
import com.hedvig.underwriter.util.logger
import org.springframework.stereotype.Service

@Service
class SwedishBankIdSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        val quoteIds = quotes.map { it.id }

        val ssn = quotes.safelyGetSsn()

        val isSwitching = quotes.any { quote -> quote.currentInsurer != null}

        val signSessionId = signSessionRepository.insert(quoteIds)

        val ip = signData.ipAddress ?: run {
            logger.error("Trying to sign swedish quotes without an ip address [Quotes: $quoteIds]")
            "127.0.0.1"
        }

        val response = memberService.startSwedishBankIdSign(
            quotes.safelyGetMemberId(),
            signSessionId,
            NationalIdentification(
                ssn,
                Nationality.SWEDEN
            ),
            ip,
            isSwitching
        )

        return response.autoStartToken?.let { autoStartToken ->
            StartSignResponse.SwedishBankIdSession(autoStartToken)
        } ?: StartSignErrors.failedToStartSign(response.internalErrorMessage!!)
    }

    override fun getSignMethod(quotes: List<Quote>) = SignMethod.SWEDISH_BANK_ID
}
