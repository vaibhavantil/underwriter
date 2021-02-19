package com.hedvig.underwriter.web

import arrow.core.Right
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsMemberAlreadySignedResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationServiceClient
import com.hedvig.underwriter.serviceIntegration.priceEngine.PriceEngineService
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.CreateContractResponse
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.javamoney.moneta.Money
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForObject
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random.Default.nextLong

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RapioNorwayIntegrationTest {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @MockkBean(relaxed = true)
    lateinit var notificationServiceClient: NotificationServiceClient

    @MockkBean
    lateinit var priceEngineService: PriceEngineService

    @MockkBean
    lateinit var debtChecker: DebtChecker

    @MockkBean
    lateinit var memberService: MemberService

    @MockkBean
    lateinit var productPricingService: ProductPricingService

    @Autowired
    lateinit var quoteService: QuoteService

    @Test
    fun `Create travel quote and sign it successfully`() {

        val memberId = nextLong(Long.MAX_VALUE).toString()
        val agreementId = UUID.randomUUID()
        val contractId = UUID.randomUUID()
        val now = Instant.now()
        val today = LocalDate.now()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { priceEngineService.queryNorwegianTravelPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(12, "NOK")
        )
        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { memberService.createMember() } returns memberId
        every { memberService.updateMemberSsn(any(), any()) } returns Unit
        every { productPricingService.createContractsFromQuotes(any(), any(), any()) } returns listOf(
            CreateContractResponse(UUID.randomUUID(), agreementId, contractId)
        )
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1L, true))
        every { memberService.finalizeOnboarding(any(), any()) } returns Unit

        val quoteRequest = """
            {
                "firstName":null,
                "lastName":null,
                "currentInsurer":null,
                "birthDate":"1912-12-12",
                "ssn":null,
                "quotingPartner":"HEDVIG",
                "productType":"TRAVEL",
                "incompleteQuoteData":{
                    "type":"norwegianTravel",
                    "coInsured":1,
                    "youth":false
                },
                "shouldComplete":true,
                "underwritingGuidelinesBypassedBy":null
            }
        """.trimIndent()

        val quoteResponse = postJson<CompleteQuoteResponseDto>("/_/v1/quotes", quoteRequest)!!

        assertNotNull(quoteResponse.id)
        assertEquals("12", quoteResponse.price.toString())
        assertTrue(quoteResponse.validTo.isAfter(now))

        val signRequest = """
            {
                "name": {
                    "firstName": "Apan",
                    "lastName": "Apansson"
                },
                "ssn": "12121212345",
                "startDate": "$today",
                "email": "apan@apansson.se"
            }
        """.trimIndent()

        val signResponse = postJson<SignedQuoteResponseDto>("/_/v1/quotes/${quoteResponse.id}/sign", signRequest)!!

        assertNotNull(signResponse.id)
        assertEquals(memberId, signResponse.memberId)

        val quote = restTemplate.getForObject("/_/v1/quotes/${quoteResponse.id}", Quote::class.java)

        assertEquals(quoteResponse.id, quote.id)
        assertTrue(quote.createdAt.isAfter(now))
        assertEquals(quoteResponse.price, quote.price)
        assertEquals("TRAVEL", quote.productType.name)
        assertEquals("SIGNED", quote.state.name)
        assertEquals("RAPIO", quote.initiatedFrom.name)
        assertEquals("HEDVIG", quote.attributedTo.name)
        assertTrue(quote.data is NorwegianTravelData)
        assertEquals(today, quote.startDate)
        assertEquals(30 * 24 * 60 * 60, quote.validity)
        assertNull(quote.breachedUnderwritingGuidelines)
        assertNull(quote.underwritingGuidelinesBypassedBy)
        assertEquals(memberId, quote.memberId)
        assertEquals(agreementId, quote.agreementId)
        assertEquals(contractId, quote.contractId)

        val data = quote.data as NorwegianTravelData
        assertEquals("12121212345", data.ssn)
        assertEquals("1912-12-12", data.birthDate.toString())
        assertEquals("Apan", data.firstName)
        assertEquals("Apansson", data.lastName)
        assertEquals("apan@apansson.se", data.email)
        assertEquals(null, data.phoneNumber)
        assertEquals(1, data.coInsured)
        assertEquals(false, data.isYouth)
    }

    @Test
    fun `Create travel quote and sign it without ssn should fail`() {

        val memberId = nextLong(Long.MAX_VALUE).toString()
        val agreementId = UUID.randomUUID()
        val contractId = UUID.randomUUID()
        val now = Instant.now()
        val today = LocalDate.now()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { priceEngineService.queryNorwegianTravelPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(12, "NOK")
        )
        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { memberService.createMember() } returns memberId
        every { memberService.updateMemberSsn(any(), any()) } returns Unit
        every { productPricingService.createContractsFromQuotes(any(), any(), any()) } returns listOf(
            CreateContractResponse(UUID.randomUUID(), agreementId, contractId)
        )
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1L, true))
        every { memberService.finalizeOnboarding(any(), any()) } returns Unit

        val quoteRequest = """
            {
                "firstName":null,
                "lastName":null,
                "currentInsurer":null,
                "birthDate":"1912-12-12",
                "ssn":null,
                "quotingPartner":"HEDVIG",
                "productType":"TRAVEL",
                "incompleteQuoteData":{
                    "type":"norwegianTravel",
                    "coInsured":1,
                    "youth":false
                },
                "shouldComplete":true,
                "underwritingGuidelinesBypassedBy":null
            }
        """.trimIndent()

        val quoteResponse = postJson<CompleteQuoteResponseDto>("/_/v1/quotes", quoteRequest)!!

        assertNotNull(quoteResponse.id)
        assertEquals("12", quoteResponse.price.toString())
        assertTrue(quoteResponse.validTo.isAfter(now))

        val signRequest = """
            {
                "name": {
                    "firstName": "Apan",
                    "lastName": "Banansson"
                },
                "ssn": "12121212345",
                "startDate": "$today",
                "email": "apan@apansson.se"
            }
        """.trimIndent()

        assertThrows(RuntimeException::class.java) {
            postJson<SignedQuoteResponseDto>("/_/v1/quotes/${quoteResponse.id}/sign", signRequest.replace("12121212345", ""))!!
        }
    }

    @Test
    fun `Create home content quote and sign it successfully`() {

        val memberId = nextLong(Long.MAX_VALUE).toString()
        val agreementId = UUID.randomUUID()
        val contractId = UUID.randomUUID()
        val now = Instant.now()
        val today = LocalDate.now()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(12, "NOK")
        )
        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { memberService.createMember() } returns memberId
        every { memberService.updateMemberSsn(any(), any()) } returns Unit
        every { productPricingService.createContractsFromQuotes(any(), any(), any()) } returns listOf(
            CreateContractResponse(UUID.randomUUID(), agreementId, contractId)
        )
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1L, true))
        every { memberService.finalizeOnboarding(any(), any()) } returns Unit

        val quoteRequest = """
            {
                "firstName": null,
                "lastName": null,
                "currentInsurer": null,
                "birthDate": "1988-01-01",
                "ssn": null,
                "quotingPartner": "HEDVIG",
                "productType": "HOME_CONTENT",
                "incompleteQuoteData": {
                    "type": "norwegianHomeContents",
                    "street": "ApGatan",
                    "zipCode": "1234",
                    "city": "ApCity",
                    "livingSpace": 122,
                    "coInsured": 0,
                    "youth": false,
                    "subType": "OWN"
                },
                "shouldComplete": true,
                "underwritingGuidelinesBypassedBy": null
            }
        """.trimIndent()

        val quoteResponse = postJson<CompleteQuoteResponseDto>("/_/v1/quotes", quoteRequest)!!

        assertNotNull(quoteResponse.id)
        assertEquals("12", quoteResponse.price.toString())
        assertTrue(quoteResponse.validTo.isAfter(now))

        val signRequest = """
            {
                "name": {
                    "firstName": "Apan",
                    "lastName": "Apansson"
                },
                "ssn": "12121212345",
                "startDate": "$today",
                "email": "apan@apansson.se"
            }
        """.trimIndent()

        val signResponse = postJson<SignedQuoteResponseDto>("/_/v1/quotes/${quoteResponse.id}/sign", signRequest)!!

        assertNotNull(signResponse.id)
        assertEquals(memberId, signResponse.memberId)

        val quote = restTemplate.getForObject("/_/v1/quotes/${quoteResponse.id}", Quote::class.java)

        assertEquals(quoteResponse.id, quote.id)
        assertTrue(quote.createdAt.isAfter(now))
        assertEquals(quoteResponse.price, quote.price)
        assertEquals("HOME_CONTENT", quote.productType.name)
        assertEquals("SIGNED", quote.state.name)
        assertEquals("RAPIO", quote.initiatedFrom.name)
        assertEquals("HEDVIG", quote.attributedTo.name)
        assertTrue(quote.data is NorwegianHomeContentsData)
        assertEquals(today, quote.startDate)
        assertEquals(30 * 24 * 60 * 60, quote.validity)
        assertNull(quote.breachedUnderwritingGuidelines)
        assertNull(quote.underwritingGuidelinesBypassedBy)
        assertEquals(memberId, quote.memberId)
        assertEquals(agreementId, quote.agreementId)
        assertEquals(contractId, quote.contractId)

        val data = quote.data as NorwegianHomeContentsData
        assertEquals("12121212345", data.ssn)
        assertEquals("1988-01-01", data.birthDate.toString())
        assertEquals("Apan", data.firstName)
        assertEquals("Apansson", data.lastName)
        assertEquals("apan@apansson.se", data.email)
        assertEquals(null, data.phoneNumber)
        assertEquals("ApGatan", data.street)
        assertEquals("ApCity", data.city)
        assertEquals("1234", data.zipCode)
        assertEquals(122, data.livingSpace)
        assertEquals(0, data.coInsured)
        assertEquals(false, data.isYouth)
        assertEquals("OWN", data.type.name)
        assertEquals(null, data.internalId)
    }

    private inline fun <reified T : Any> postJson(url: String, data: String): T? {

        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        headers.contentType = MediaType.APPLICATION_JSON

        return restTemplate
            .postForObject(
                url,
                HttpEntity(
                    data, headers
                )
            )
    }
}
