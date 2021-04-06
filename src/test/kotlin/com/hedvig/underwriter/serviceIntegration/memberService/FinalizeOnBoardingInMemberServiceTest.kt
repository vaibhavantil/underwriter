package com.hedvig.underwriter.serviceIntegration.memberService

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.FinalizeOnBoardingRequest
import com.hedvig.underwriter.testhelp.databuilder.DanishHomeContentsDataBuilder
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

    @Test
    fun `finalizeOnBoarding works for DanishHomeContent`() {

        val client = mockk<MemberServiceClient>()
        val cut = MemberServiceImpl(client, ObjectMapper())

        val x = slot<FinalizeOnBoardingRequest>()
        every { client.finalizeOnBoarding(any(), capture(x)) } returns ResponseEntity.ok("")

        val quote = quote {
            memberId = "1337"
            data = DanishHomeContentsDataBuilder(
                phoneNumber = "123456",
                email = "someemail@hotmail.com",
                floor = "5",
                apartment = "2 th"
            )
        }

        cut.finalizeOnboarding(quote, "someemail@hotmail.com")

        assertThat(x.captured.email).isEqualTo("someemail@hotmail.com")
        assertThat(x.captured.phoneNumber).isEqualTo("123456")
        assertThat(x.captured.address?.floor).isEqualTo(5)
        assertThat(x.captured.address?.apartmentNo).isEqualTo("2 th")
    }

    @Test
    fun `finalizeOnBoarding handles if floor is "st" and cannot be converted to Int for DanishHomeContent`() {

        val client = mockk<MemberServiceClient>()
        val cut = MemberServiceImpl(client, ObjectMapper())

        val x = slot<FinalizeOnBoardingRequest>()
        every { client.finalizeOnBoarding(any(), capture(x)) } returns ResponseEntity.ok("")

        val quote = quote {
            memberId = "1337"
            data = DanishHomeContentsDataBuilder(
                phoneNumber = "123456",
                email = "someemail@hotmail.com",
                floor = "st",
                apartment = "2 th"
            )
        }

        cut.finalizeOnboarding(quote, "someemail@hotmail.com")

        assertThat(x.captured.email).isEqualTo("someemail@hotmail.com")
        assertThat(x.captured.phoneNumber).isEqualTo("123456")
        assertThat(x.captured.address?.floor).isEqualTo(0)
        assertThat(x.captured.address?.apartmentNo).isEqualTo("2 th")
    }
}
