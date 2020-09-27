package com.hedvig.underwriter.web

import com.hedvig.underwriter.model.QuoteRepositoryInMemory
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.QuoteServiceImpl
import com.hedvig.underwriter.service.Underwriter
import com.hedvig.underwriter.service.UnderwriterImpl
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension

@TestConfiguration
class QuoteUpdateWithSchemaTestConfiguration() {

    @Bean
    fun debtChecker(): DebtChecker = mockk(relaxed = true)

    @Bean
    fun underwriter(debtChecker: DebtChecker): Underwriter =
        UnderwriterImpl(debtChecker, mockk(relaxed = true))

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
@WebMvcTest(controllers = [QuoteSchemaController::class])
@Import(QuoteUpdateWithSchemaTestConfiguration::class)
class QuoteUpdateWithSchemaTest {
    /*
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var quoteService: QuoteService

    @Autowired
    private lateinit var underwriter: Underwriter

    @Autowired
    private lateinit var debtChecker: DebtChecker
    @Test
    internal fun `first test`() {

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

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

        val q = quote as Either.Right

        val response = mockMvc.perform(
            MockMvcRequestBuilders
                .post("/_/v2/quotes/{quoteId}/update", quote.b.id)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(
                    """
                    
                """.trimIndent()
                )
        )
    }*/
}
