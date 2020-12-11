package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.model.ssn
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SwedishBankIdSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        SignUtil.requireValidSwedishQuotes(quotes)

        val quoteIds = quotes.map { it.id }

        val ssn = quotes[0].ssn

        val isSwitching = quotes[0].currentInsurer != null

        val signSessionId = signSessionRepository.insert(quoteIds)

        val ip = signData.ipAddress ?: run {
            logger.error("Trying to sign swedish quotes without an ip address [Quotes: $quoteIds]")
            "127.0.0.1"
        }

        val response = memberService.startSwedishBankIdSignQuotes(
            quotes[0].memberId!!.toLong(),
            signSessionId,
            ssn,
            ip,
            isSwitching
        )

        return response.autoStartToken?.let { autoStartToken ->
            StartSignResponse.SwedishBankIdSession(autoStartToken)
        } ?: StartSignErrors.failedToStartSign(response.internalErrorMessage!!)
    }

    companion object {
        val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
