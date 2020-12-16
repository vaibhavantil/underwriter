package com.hedvig.underwriter.service.quotesSignDataStrategies

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.service.quotesSignDataStrategies.StrategyHelper.createSignData
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterStartSignSessionResponse
import com.hedvig.underwriter.testhelp.databuilder.QuoteBuilder
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID

class SimpleSignStrategyTest {

    private val signSessionRepository: SignSessionRepository = mockk()
    private val memberService: MemberService = mockk()

    private val cut = SimpleSignStrategy(signSessionRepository, memberService)

    @Test
    fun `start sign calls member service startSimpleSign successfully and returns SimpleSignSession`() {
        val sessionId = UUID.randomUUID()
        every {
            signSessionRepository.insert(any())
        } returns sessionId
        every {
            memberService.startSimpleSign(
                any(),
                sessionId,
                any()
            )
        } returns UnderwriterStartSignSessionResponse.SimpleSign(true)

        val result = cut.startSign(listOf(
            QuoteBuilder(
            memberId = "1234"
        ).build()), createSignData())

        assertThat(result).isInstanceOf(StartSignResponse.SimpleSignSession::class)
        require(result is StartSignResponse.SimpleSignSession)
        assertThat(result.id).isEqualTo(sessionId)
    }

    @Test
    fun `start sign calls member service startSimpleSign failed and returns StartSignErrors`() {
        val sessionId = UUID.randomUUID()
        every {
            signSessionRepository.insert(any())
        } returns sessionId
        every {
            memberService.startSimpleSign(
                any(),
                sessionId,
                any()
            )
        } returns UnderwriterStartSignSessionResponse.SimpleSign(false, "Something went wrong")

        val result = cut.startSign(listOf(
            QuoteBuilder(
            memberId = "1234"
        ).build()), createSignData())

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo("Something went wrong")
        assertThat(result.errorCode).isEqualTo("FAILED_TO_START_SIGN")
    }

    @Test
    fun `start sign calls member service startSimpleSign failed with no error message and returns StartSignErrors`() {
        val sessionId = UUID.randomUUID()
        every {
            signSessionRepository.insert(any())
        } returns sessionId
        every {
            memberService.startSimpleSign(
                any(),
                sessionId,
                any()
            )
        } returns UnderwriterStartSignSessionResponse.SimpleSign(false)

        val result = cut.startSign(listOf(
            QuoteBuilder(
            memberId = "1234"
        ).build()), createSignData())

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class)
        require(result is StartSignResponse.FailedToStartSign)
        assertThat(result.errorMessage).isEqualTo("No error message")
        assertThat(result.errorCode).isEqualTo("FAILED_TO_START_SIGN")
    }
}
