package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.SignMethod
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.NationalIdentification
import org.springframework.stereotype.Service

@Service
class SimpleSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        val signSessionId = signSessionRepository.insert(SignMethod.SIMPLE_SIGN, quotes.map { it.id })

        val response = memberService.startSimpleSign(
            memberId = quotes.safelyGetMemberId(),
            underwriterSessionReference = signSessionId,
            nationalIdentification = NationalIdentification(
                quotes.safelyGetSsn(),
                quotes.safelyNationality()
            )
        )

        return if (response.successfullyStarted) {
            StartSignResponse.SimpleSignSession(signSessionId)
        } else {
            response.internalErrorMessage?.let {
                StartSignErrors.failedToStartSign(it)
            } ?: StartSignErrors.failedToStartSign("No error message")
        }
    }
}
