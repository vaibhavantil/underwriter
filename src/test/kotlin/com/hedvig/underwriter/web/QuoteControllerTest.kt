package com.hedvig.underwriter.web

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.service.BundleQuotesService
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.SignService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@RunWith(SpringRunner::class)
@WebMvcTest(controllers = [QuoteController::class], secure = false)
internal class QuoteControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var quoteService: QuoteService

    @MockkBean
    lateinit var signService: SignService

    @MockkBean
    lateinit var memberService: MemberService

    @MockkBean
    lateinit var bundleQuotesService: BundleQuotesService

    val createQuoteRequestJson = """
        {
            "incompleteQuoteData": {
            "type": "apartment",
                "incompleteApartmentQuoteData": {
                    "zipcode": "11216"
                }
            }
        }
    """.trimIndent()

    @Test
    fun createIncompleteQuote() {
        val request = post("/_/v1/quote")
            .content(createQuoteRequestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        every {
            quoteService.createQuote(
                any(),
                initiatedFrom = QuoteInitiatedFrom.RAPIO,
                underwritingGuidelinesBypassedBy = any(),
                updateMemberService = false
            )
        } returns Either.Right(
            CompleteQuoteResponseDto(
                UUID.fromString("2baa9736-360d-11ea-bce2-875cabb114ed"),
                BigDecimal.TEN,
                "SEK",
                Instant.now()
            )
        )

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)
    }

    @Test
    fun getQuote() {

        val uuid: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        val incompleteQuote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            state = QuoteState.INCOMPLETE,
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.COMPRICER,
            data = SwedishApartmentData(
                id = UUID.randomUUID(),
                street = "123 Baker street",
                city = "Stockholm",
                // numberOfRooms = 3,
                zipCode = "11216",
                householdSize = 1,
                livingSpace = 33,
                subType = ApartmentProductSubType.RENT,
                firstName = "Simeone",
                lastName = "null",
                ssn = "189003042342"
            ),
            breachedUnderwritingGuidelines = null,
            currentInsurer = null
        )

        every { quoteService.getQuote(uuid) } returns incompleteQuote

        mockMvc
            .perform(
                get("/_/v1/quote/$uuid")
            )
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("productType").value("APARTMENT"))
    }

    @Test
    fun getQuoteByContractId() {

        val contractId: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            state = QuoteState.SIGNED,
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.COMPRICER,
            data = SwedishApartmentData(
                id = UUID.randomUUID(),
                street = "123 Baker street",
                city = "Stockholm",
                // numberOfRooms = 3,
                zipCode = "11216",
                householdSize = 1,
                livingSpace = 33,
                subType = ApartmentProductSubType.RENT,
                firstName = "Simeone",
                lastName = "null",
                ssn = "189003042342"
            ),
            breachedUnderwritingGuidelines = null,
            currentInsurer = null,
            agreementId = contractId
        )

        every { quoteService.getQuoteByContractId(contractId) } returns quote

        mockMvc
            .perform(
                get("/_/v1/quotes/contracts/$contractId")
            )
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("productType").value("APARTMENT"))
    }

    @Test
    fun `contract not found returns with 404`() {

        val contractId: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        every { quoteService.getQuoteByContractId(contractId) } returns null

        mockMvc
            .perform(
                get("/_/v1/quotes/contracts/$contractId")
            )
            .andExpect(status().is4xxClientError)
    }

    @Test
    fun `creates DanishHomeContentQuote`() {
    }
}
