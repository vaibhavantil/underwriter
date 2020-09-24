package com.hedvig.underwriter.web

import arrow.core.Right
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepositoryInMemory
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.QuoteServiceImpl
import com.hedvig.underwriter.service.Underwriter
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.testhelp.databuilder.a
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDate
import java.util.UUID

@TestConfiguration
class QuoteUpdateWithSchemaTestConfiguration() {

    @MockkBean
    lateinit var underwriter: Underwriter

    @Bean
    fun quoteService(underwriter: Underwriter): QuoteService = QuoteServiceImpl(
        underwriter,
        mockk(),
        mockk(),
        QuoteRepositoryInMemory(),
        mockk()
    )
}

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [V2QuoteController::class])
@Import(QuoteUpdateWithSchemaTestConfiguration::class)
class QuoteUpdateWithSchemaTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var quoteService: QuoteService

    @Autowired
    private lateinit var underwriter: Underwriter

    @Test
    internal fun `first test`() {

        every {
            underwriter.createQuote(any(), any(), any(), any())
        } returns Right(
            a.QuoteBuilder(
                productType = ProductType.HOUSE,
                state = QuoteState.QUOTED,
                initiatedFrom = QuoteInitiatedFrom.HOPE
            ).w(a.SwedishHouseDataBuilder())
                .build()
        )

        val quote = quoteService.createQuote(
            QuoteRequest(
                "Firstname",
                "Lastname",
                null,
                null,
                LocalDate.of(1912, 12, 12),
                "191212121212",
                null,
                ProductType.APARTMENT,
                QuoteRequestData.SwedishHouse(
                    "Långgatan 1",
                    "12345",
                    "Stockholm",
                    123,
                    3,
                    0,
                    1970,
                    1,
                    listOf(),
                    false,
                    0
                ), "1337",
                null,
                null,
                null
            ),
            UUID.randomUUID(),
            QuoteInitiatedFrom.HOPE,
            null,
            false
        )
    }
}
