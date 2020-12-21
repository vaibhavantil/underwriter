package com.hedvig.underwriter.service

import arrow.core.Right
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteRepositoryImpl
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.model.SignSessionRepositoryImpl
import com.hedvig.underwriter.service.model.CompleteSignSessionData
import com.hedvig.underwriter.service.quotesSignDataStrategies.SignStrategyService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.CreateContractResponse
import com.hedvig.underwriter.testhelp.JdbiRule
import com.hedvig.underwriter.testhelp.databuilder.quote
import io.mockk.every
import io.mockk.mockk
import org.jdbi.v3.jackson2.Jackson2Config
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class CompletedSignSessionTest {

    @get:Rule
    val jdbiRule = JdbiRule.create()

    private lateinit var signSessionRepository: SignSessionRepository
    private lateinit var quoteService: QuoteService
    private lateinit var productPricingService: ProductPricingService
    private lateinit var quoteRepository: QuoteRepository
    private lateinit var memberService: MemberService
    private lateinit var sut: SignServiceImpl
    private lateinit var signStrategyService: SignStrategyService

    @Before
    fun setUp() {
        jdbiRule.jdbi.getConfig(Jackson2Config::class.java).mapper =
            ObjectMapper().registerModule(KotlinModule())

        signSessionRepository = SignSessionRepositoryImpl(jdbiRule.jdbi)
        quoteRepository = QuoteRepositoryImpl(jdbi = jdbiRule.jdbi)
        quoteService = mockk()
        productPricingService = mockk()
        memberService = mockk()
        signStrategyService = mockk()
        sut = SignServiceImpl(
            quoteService,
            quoteRepository,
            memberService,
            productPricingService,
            signSessionRepository,
            signStrategyService,
            mockk(),
            mockk()
        )
    }

    @Test
    fun `store agreementId in signedProductId column`() {

        val quoteId = UUID.randomUUID()

        val quote = quote {
            id = quoteId
            memberId = "1338"
        }
        quoteRepository.insert(quote)

        val signSessionId = signSessionRepository.insert(listOf(quoteId))

        val agreementId = UUID.randomUUID()
        val contractId = UUID.randomUUID()
        every { productPricingService.createContractsFromQuotes(any(), any(), any()) } returns listOf(
            CreateContractResponse(quote.id, agreementId, contractId)
        )

        every { memberService.signQuote(1338L, any()) } returns Right(UnderwriterQuoteSignResponse(123L, true))

        sut.completedSignSession(
            signSessionId,
            CompleteSignSessionData.SwedishBankIdDataComplete(
                "referenceToken",
                "signature",
                "oscpResponse"
            )
        )

        assertThat(quoteRepository.find(quote.id)!!.agreementId).isEqualTo(agreementId)
    }

    @Test
    fun `store contractId`() {

        val quoteId = UUID.randomUUID()

        val quote = quote {
            id = quoteId
            memberId = "1339"
        }
        quoteRepository.insert(quote)

        val signSessionId = signSessionRepository.insert(listOf(quoteId))

        val agreementId = UUID.randomUUID()
        val contractId = UUID.randomUUID()
        every { productPricingService.createContractsFromQuotes(any(), any(), any()) } returns listOf(
            CreateContractResponse(quote.id, agreementId, contractId)
        )

        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(123L, true))

        sut.completedSignSession(
            signSessionId,
            CompleteSignSessionData.SwedishBankIdDataComplete(
                "referenceToken",
                "signature",
                "oscpResponse"
            )
        )

        assertThat(quoteRepository.find(quote.id)!!.contractId).isEqualTo(contractId)
    }
}
