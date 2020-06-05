package com.hedvig.underwriter.service

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hedvig.underwriter.model.QuoteRepositoryImpl
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.testhelp.JdbiRule
import com.hedvig.underwriter.testhelp.databuilder.a
import io.mockk.mockk
import java.util.UUID
import org.jdbi.v3.jackson2.Jackson2Config
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FindQuoteByContractIdTest {

    @get:Rule
    val jdbiRule = JdbiRule.create()

    @Before
    fun setUp() {
        jdbiRule.jdbi.getConfig(Jackson2Config::class.java).mapper =
            ObjectMapper().registerModule(KotlinModule())
    }

    @Test
    fun `find quote by contract id`() {

        val quoteRepository = QuoteRepositoryImpl(jdbiRule.jdbi)
        val sut = QuoteServiceImpl(
            mockk(),
            mockk(),
            mockk(),
            quoteRepository
        )

        val contractId = UUID.randomUUID()
        quoteRepository.insert(a.QuoteBuilder(state = QuoteState.SIGNED, signedProductId = contractId).build())

        val result = sut.getQuoteByContractId(contractId)
        assertThat(result).isNotNull().all {
            transform { it.signedProductId }.isEqualTo(contractId)
        }
    }

    @Test
    fun `no quote exists with contract id`() {

        val quoteRepository = QuoteRepositoryImpl(jdbiRule.jdbi)
        val sut = QuoteServiceImpl(
            mockk(),
            mockk(),
            mockk(),
            quoteRepository
        )

        val contractId = UUID.randomUUID()

        val result = sut.getQuoteByContractId(contractId)
        assertThat(result).isNull()
    }
}
