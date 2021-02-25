package com.hedvig.underwriter.web

import arrow.core.Either
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.underwriter.model.ContractType
import com.hedvig.underwriter.service.QuoteSchemaService
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteSchema
import com.hedvig.underwriter.testhelp.databuilder.NorwegianTravelDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.quote
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@RunWith(SpringRunner::class)
@WebMvcTest(controllers = [QuoteSchemaController::class])
internal class QuoteSchemaControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var quoteSchemaService: QuoteSchemaService

    @MockkBean
    private lateinit var quoteService: QuoteService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val QUOTE_ID = UUID.fromString("3a6fc43c-7db3-482b-93ca-3553c8ae8694")
    private val MEMBER_ID = "12345"

    private val SCHEMA_JSON_STRING =
        """
            {
              "${'$'}schema": "http://json-schema.org/draft-07/schema#",
              "type": "object",
              "properties": {
                "numberCoInsured": {
                  "type": "integer",
                  "title": "Number Co-Insured",
                  "minimum": 0
                },
                "isYouth": {
                  "type": "boolean",
                  "title": "Is Youth",
                  "default": false
                }
              },
              "required": [
                "numberCoInsured",
                "isYouth"
              ],
              "${'$'}id": "NorwegianTravel"
            }
        """.trimIndent()

    private lateinit var SCHEMA: JsonNode

    private val SCHEMA_DATA = QuoteSchema.NorwegianTravel(
        numberCoInsured = 1,
        isYouth = false
    )

    private val SCHEMA_DATA_JSON = """
        {
            "id": "NorwegianTravel",
            "numberCoInsured": ${SCHEMA_DATA.numberCoInsured},
            "isYouth": ${SCHEMA_DATA.isYouth} 
        }
    """.trimIndent()

    private val QUOTE = quote {
        id = QUOTE_ID
        memberId = MEMBER_ID
        data = NorwegianTravelDataBuilder()
    }

    private val QUOTE_FROM_SCHEMA_DATA = quote {
        id = QUOTE_ID
        memberId = MEMBER_ID
        data = NorwegianTravelDataBuilder(
            coInsured = SCHEMA_DATA.numberCoInsured,
            isYouth = SCHEMA_DATA.isYouth
        )
    }

    @Before
    fun setup() {
        SCHEMA = objectMapper.readTree(SCHEMA_JSON_STRING)
    }

    @Test
    fun `gets status 2XX with schema by quoteId when quote exists`() {
        every {
            quoteSchemaService.getSchemaByQuoteId(QUOTE_ID)
        } returns SCHEMA
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v1/quotes/schema/{quoteId}", QUOTE_ID)
        )
        response
            .andExpect(
                status().is2xxSuccessful
            )
            .andExpect(
                MockMvcResultMatchers.content().json(SCHEMA_JSON_STRING)
            )
    }

    @Test
    fun `gets 404 with no schema by quoteId when quote does not exists`() {
        every {
            quoteSchemaService.getSchemaByQuoteId(QUOTE_ID)
        } returns null
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v1/quotes/schema/{quoteId}", QUOTE_ID)
        )
        response
            .andExpect(
                status().`is`(404)
            )
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                    {"errorCode":"NO_SUCH_QUOTE","errorMessage":"Quote $QUOTE_ID not found when getting schema","breachedUnderwritingGuidelines":null} 
                """.trimIndent()
                )
            )
    }

    @Test
    fun `gets status 2XX and schema data by quoteId when quote exists`() {
        every {
            quoteSchemaService.getSchemaDataByQuoteId(QUOTE_ID)
        } returns SCHEMA_DATA
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v1/quotes/schema/{quoteId}/data", QUOTE_ID)
        )
        response
            .andExpect(
                status().is2xxSuccessful
            )
            .andExpect(
                MockMvcResultMatchers.content().json(SCHEMA_DATA_JSON)
            )
    }

    @Test
    fun `gets 404 with no schema data by quoteId when quote does not exists`() {
        every {
            quoteSchemaService.getSchemaDataByQuoteId(QUOTE_ID)
        } returns null
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v1/quotes/schema/{quoteId}/data", QUOTE_ID)
        )
        response
            .andExpect(
                status().`is`(404)
            )
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                    {"errorCode":"NO_SUCH_QUOTE","errorMessage":"Quote $QUOTE_ID not found when getting schema data","breachedUnderwritingGuidelines":null} 
                """.trimIndent()
                )
            )
    }

    @Test
    fun `gets status 2XX and schema data by contractType`() {
        every {
            quoteSchemaService.getSchemaByContractType(ContractType.NORWEGIAN_TRAVEL)
        } returns SCHEMA
        val response = mockMvc.perform(
            MockMvcRequestBuilders.get("/_/v1/quotes/schema/contract/{contractType}", ContractType.NORWEGIAN_TRAVEL)
        )
        response
            .andExpect(
                status().is2xxSuccessful
            )
            .andExpect(
                MockMvcResultMatchers.content().json(SCHEMA_JSON_STRING)
            )
    }

    @Test
    fun `gets status 404 if no quote is available for update`() {
        every {
            quoteService.getQuote(QUOTE_ID)
        } returns null
        val response = mockMvc.perform(
            MockMvcRequestBuilders.post(
                "/_/v1/quotes/schema/{quoteId}/update?underwritingGuidelinesBypassedBy=null",
                QUOTE_ID,
                null
            )
                .content(SCHEMA_DATA_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
        response
            .andExpect(
                status().`is`(404)
            )
            .andExpect(
                MockMvcResultMatchers.content().json(
                    """
                    {"errorCode":"NO_SUCH_QUOTE","errorMessage":"Quote $QUOTE_ID not found when updating quote via schema data","breachedUnderwritingGuidelines":null} 
                """.trimIndent()
                )
            )
    }

    @Test
    fun `gets updated quote and 2XX status when updating an available quote`() {
        every {
            quoteService.getQuote(QUOTE_ID)
        } returns QUOTE
        val quoteRequest = QuoteRequest.from(QUOTE, SCHEMA_DATA)
        every {
            quoteService.updateQuote(
                quoteRequest = quoteRequest,
                id = QUOTE_ID,
                underwritingGuidelinesBypassedBy = any()
            )
        } returns Either.Right(QUOTE_FROM_SCHEMA_DATA)

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post(
                "/_/v1/quotes/schema/{quoteId}/update?underwritingGuidelinesBypassedBy=null",
                QUOTE_ID,
                null
            )
                .content(SCHEMA_DATA_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
        response
            .andExpect(
                status().is2xxSuccessful
            )
            .andExpect(
                MockMvcResultMatchers.content().json(
                    objectMapper.writeValueAsString(QUOTE_FROM_SCHEMA_DATA)
                )
            )
    }

    @Test
    fun `gets created quote and 2XX status when creating a quote for member`() {
        val quoteRequest = QuoteRequest.from(MEMBER_ID, SCHEMA_DATA)
        val completeQuoteResponse = CompleteQuoteResponseDto(
            id = QUOTE_ID,
            price = BigDecimal.TEN,
            currency = "SEK",
            validTo = Instant.now().plusSeconds(3600 * 24 * 31)
        )
        every {
            quoteService.createQuoteForNewContractFromHope(
                quoteRequest = quoteRequest,
                underwritingGuidelinesBypassedBy = any()
            )
        } returns Either.Right(completeQuoteResponse)

        val response = mockMvc.perform(
            MockMvcRequestBuilders.post(
                "/_/v1/quotes/schema/{memberId}/create?underwritingGuidelinesBypassedBy=null",
                MEMBER_ID,
                null
            )
                .content(SCHEMA_DATA_JSON)
                .contentType(MediaType.APPLICATION_JSON)
        )
        response
            .andExpect(
                status().is2xxSuccessful
            )
            .andExpect(
                MockMvcResultMatchers.content().json(
                    objectMapper.writeValueAsString(completeQuoteResponse)
                )
            )
    }
}
