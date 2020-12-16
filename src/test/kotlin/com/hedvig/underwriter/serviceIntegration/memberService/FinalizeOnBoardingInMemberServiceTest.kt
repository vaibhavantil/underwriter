package com.hedvig.underwriter.serviceIntegration.memberService

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.FinalizeOnBoardingRequest
import com.hedvig.underwriter.testhelp.databuilder.NorwegianHomeContentDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.NorwegianTravelDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.quote
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity

internal class FinalizeOnBoardingInMemberServiceTest {

    @Test
    fun `finalizeOnBoarding forwards email and phonenumber`() {

        val client = mockk<MemberServiceClient>()
        val cut = MemberServiceImpl(client, ObjectMapper())

        val x = slot<FinalizeOnBoardingRequest>()
        every { client.finalizeOnBoarding(any(), capture(x)) } returns ResponseEntity.ok("")

        val quote = quote {
            memberId = "1337"
            data = NorwegianHomeContentDataBuilder(
                phoneNumber = "123456",
                email = "someemail@hotmail.com"
            )
        }

        cut.finalizeOnboarding(quote, "someemail@hotmail.com")

        assertThat(x.captured.email).isEqualTo("someemail@hotmail.com")
        assertThat(x.captured.phoneNumber).isEqualTo("123456")
    }

    @Test
    fun `finalizeOnBoarding forwards email and phonenumber for NorwegianTravel`() {

        val client = mockk<MemberServiceClient>()
        val cut = MemberServiceImpl(client, ObjectMapper())

        val x = slot<FinalizeOnBoardingRequest>()
        every { client.finalizeOnBoarding(any(), capture(x)) } returns ResponseEntity.ok("")

        val quote = quote {
            memberId = "1337"
            data = NorwegianTravelDataBuilder(
                phoneNumber = "123456",
                email = "someemail@hotmail.com"
            )
        }

        cut.finalizeOnboarding(quote, "someemail@hotmail.com")

        assertThat(x.captured.email).isEqualTo("someemail@hotmail.com")
        assertThat(x.captured.phoneNumber).isEqualTo("123456")
    }
}
