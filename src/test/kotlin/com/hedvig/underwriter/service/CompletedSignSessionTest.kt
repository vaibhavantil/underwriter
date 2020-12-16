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
import com.hedvig.underwriter.testhelp.databuilder.QuoteBuilder
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.jdbi.v3.jackson2.Jackson2Config
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CompletedSignSessionTest {

    @get:Rule
    val jdbiRule = JdbiRule.create()

    lateinit var signSessionRepository: SignSessionRepository
    lateinit var quoteService: QuoteService
    lateinit var productPricingService: ProductPricingService
    lateinit var quoteRepository: QuoteRepository
    lateinit var memberService: MemberService
    lateinit var sut: SignServiceImpl
    lateinit var signStrategyService: SignStrategyService

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

        val quote = QuoteBuilder(
            id = quoteId,
            memberId = "1338"
        ).build()
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

        val quote = QuoteBuilder(
            id = quoteId,
            memberId = "1339"
        ).build()
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
