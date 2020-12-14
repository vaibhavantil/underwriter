package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import org.springframework.stereotype.Service

@Service
class SimpleSignStrategy(
    private val signSessionRepository: SignSessionRepository,
    private val memberService: MemberService
) : SignStrategy {
    override fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse {
        TODO()

        val signSessionId = signSessionRepository.insert(quotes.map { it.id })

    }
}
