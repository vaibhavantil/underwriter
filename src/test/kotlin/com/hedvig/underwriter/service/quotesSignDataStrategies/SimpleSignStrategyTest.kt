package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import io.mockk.mockk

class SimpleSignStrategyTest {

    private val signSessionRepository: SignSessionRepository = mockk()
    private val memberService: MemberService = mockk()
}
