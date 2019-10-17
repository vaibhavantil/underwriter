package com.hedvig.underwriter.web

import com.hedvig.underwriter.model.HomeData
import com.hedvig.underwriter.model.HomeProductSubType
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.service.QuoteService
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
import java.time.Instant
import java.util.UUID

@RunWith(SpringRunner::class)
@WebMvcTest(controllers = [QuoteBuilderController::class], secure = false)
internal class QuoteBuilderControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var quoteService: QuoteService

    val createQuoteRequestJson = """
        {
            "quoteState": "INCOMPLETE",
            "dateStartedRecievingQuoteInfo": "2019-09-17T13:32:00.783981Z",
            "productType": "HOME",
            "incompleteQuoteData": {
                "incompleteHomeQuoteData": {
                    "zipcode": "11216"
                }
            }
        }
    """.trimIndent()

    @Ignore
    @Test
    fun createIncompleteQuote() {
        val request = post("/_/v1/quote/create")
            .content(createQuoteRequestJson)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)

        val result = mockMvc.perform(request)

        result.andExpect(status().is2xxSuccessful)
    }

    @Ignore
    @Test
    fun getQuote() {

        val uuid: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        val incompleteQuote = Quote(
            createdAt = Instant.now(),
            state = QuoteState.INCOMPLETE,
            productType = ProductType.HOME,
            data = HomeData(
                street = "123 Baker street",
                city = "Stockholm",
                //numberOfRooms = 3,
                zipCode = "11216",
                householdSize = 1,
                livingSpace = 33,
                subType = HomeProductSubType.RENT,
                isStudent = false,
                firstName = "Simeone",
                lastName = "null",
                ssn = "189003042342"
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,
            id = UUID.randomUUID(),
            currentInsurer = null
        )

        Mockito.`when`(quoteService.getQuote(uuid))
            .thenReturn(incompleteQuote)

        mockMvc
            .perform(
                get("/_/v1/quote/71919787-70d2-4614-bd4a-26427861991d")
            )
            .andExpect(status().is2xxSuccessful)
            .andExpect(jsonPath("quoteState").value("INCOMPLETE"))
            .andExpect(jsonPath("productType").value("HOME"))
            .andExpect(jsonPath("incompleteQuoteData.numberOfRooms").value(3))
    }

    @Ignore
    @Test
    fun createCompleteQuote() {

        val uuid: UUID = UUID.fromString("71919787-70d2-4614-bd4a-26427861991d")

        val incompleteQuote = Quote(
            createdAt = Instant.now(),
            state = QuoteState.INCOMPLETE,
            productType = ProductType.HOME,
            data =
            HomeData(
                street = "123 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "nul",
                subType = HomeProductSubType.BRF,
                isStudent = false,
                firstName = "null",
                lastName = "null"
            ),
            initiatedFrom = QuoteInitiatedFrom.APP,

            id = UUID.randomUUID(),
            currentInsurer = null
        )

        Mockito.`when`(quoteService.getQuote(uuid))
            .thenReturn(incompleteQuote)

        mockMvc
            .perform(
                post("/_/v1/quote/71919787-70d2-4614-bd4a-26427861991d/completeQuote")
            )
            .andExpect(status().is2xxSuccessful)
    }

    @Ignore
    @Test
    fun shouldNotCompleteQuoteIfDataIsIncomplete() {
//        TODO
    }
}