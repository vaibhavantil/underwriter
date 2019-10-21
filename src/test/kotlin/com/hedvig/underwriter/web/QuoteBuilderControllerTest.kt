package com.hedvig.underwriter.web

import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import java.time.Instant
import java.util.UUID
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@RunWith(SpringRunner::class)
@WebMvcTest(controllers = [QuoteController::class], secure = false)
internal class QuoteBuilderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var quoteService: QuoteService

    @MockBean
    lateinit var memberService: MemberService

    val createQuoteRequestJson = """
        {
            "dateStartedRecievingQuoteInfo": "2019-09-17T13:32:00.783981Z",
            "apartmentProductSubType": "RENT",
            "incompleteQuoteData": {
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

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)
    }

    @Test
    fun getQuote() {

        val uuid: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        val incompleteQuote = Quote(
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            data = ApartmentData(
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
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.COMPRICER,
            id = UUID.randomUUID(),
            currentInsurer = null,
            state = QuoteState.INCOMPLETE
        )

        Mockito.`when`(quoteService.getQuote(uuid))
            .thenReturn(incompleteQuote)

        mockMvc
            .perform(
                get("/_/v1/quote/$uuid")
            )
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("productType").value("APARTMENT"))
    }

    @Ignore
    @Test
    fun createCompleteQuote() {

        val uuid: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        val incompleteQuote = Quote(
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            data =
            ApartmentData(
                street = "123 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "nul",
                subType = ApartmentProductSubType.BRF,
                firstName = "null",
                lastName = "null",
                id = UUID.randomUUID()
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.COMPRICER,
            id = UUID.randomUUID(),
            currentInsurer = null,
            state = QuoteState.INCOMPLETE
        )

        Mockito.`when`(quoteService.getQuote(uuid))
            .thenReturn(incompleteQuote)

        mockMvc
            .perform(
                post("/_/v1/quotes/71919787-70d2-4614-bd4a-26427861991d/completeQuote")
            )
            .andExpect(status().is2xxSuccessful)
    }

    @Ignore
    @Test
    fun shouldNotCompleteQuoteIfDataIsIncomplete() {
//        TODO
    }
}
