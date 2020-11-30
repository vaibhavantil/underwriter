package com.hedvig.underwriter.service

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hedvig.underwriter.model.QuoteRepositoryImpl
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.AddAgreementResponse
import com.hedvig.underwriter.testhelp.JdbiRule
import com.hedvig.underwriter.testhelp.databuilder.a
import com.hedvig.underwriter.web.dtos.AddAgreementFromQuoteRequest
import io.mockk.every
import io.mockk.mockk
import org.jdbi.v3.jackson2.Jackson2Config
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID

class AddAgreementToContractTest {

    @get:Rule
    val jdbiRule = JdbiRule.create()

    @Before
    fun setUp() {
        jdbiRule.jdbi.getConfig(Jackson2Config::class.java).mapper =
            ObjectMapper().registerModule(KotlinModule())
    }

    @Test
    fun `saves contractId to quote`() {
        val quoteRepository = QuoteRepositoryImpl(jdbiRule.jdbi)
        val productPricingService = mockk<ProductPricingService>()
        val sut = QuoteServiceImpl(mockk(), mockk(), productPricingService, quoteRepository, mockk(), mockk())

        val quoteId = UUID.randomUUID()

        quoteRepository.insert(a.QuoteBuilder(id = quoteId).build())

        val agreementId = UUID.randomUUID()
        val contractId = UUID.randomUUID()
        every { productPricingService.addAgreementFromQuote(any(), any(), any()) } returns AddAgreementResponse(
            quoteId,
            agreementId,
            contractId
        )

        sut.addAgreementFromQuote(
            AddAgreementFromQuoteRequest(
                quoteId,
                null,
                null,
                null,
                null
            ),
            null
        )

        assertThat(quoteRepository.find(quoteId)).isNotNull().all {
            transform { it.contractId }.isEqualTo(contractId)
            transform { it.agreementId }.isEqualTo(agreementId)
        }
    }
}
