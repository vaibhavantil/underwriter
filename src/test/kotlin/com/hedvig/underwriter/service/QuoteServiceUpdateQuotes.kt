package com.hedvig.underwriter.service

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isNullOrEmpty
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.service.quoteStrategies.QuoteStrategyService
import com.hedvig.underwriter.testhelp.databuilder.SwedishApartmentDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.SwedishApartmentQuoteRequestBuilder
import com.hedvig.underwriter.testhelp.databuilder.quote
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class QuoteServiceUpdateQuotes {

    @Test
    fun clear_old_breached_underwriting_guidelines() {

        val quoteRepository = mockk<QuoteRepository>()

        val quoteStrategyService = mockk<QuoteStrategyService>(relaxed = true)
        val cut = QuoteServiceImpl(
            UnderwriterImpl(mockk(relaxed = true), quoteStrategyService, mockk(relaxed = true)),
            mockk(relaxed = true),
            mockk(relaxed = true),
            quoteRepository,
            mockk(relaxed = true),
            quoteStrategyService
        )

        val request = SwedishApartmentQuoteRequestBuilder()
        every { quoteRepository.find(any()) } returns quote {
            id = request.id
            data = SwedishApartmentDataBuilder()
            breachedUnderwritingGuidelines = listOf("UW_GL_HIT")
        }

        every { quoteRepository.update(any(), any()) } returnsArgument 0

        val result = cut.updateQuote(request.build(), request.id)
        require(result is Either.Right)
        assertThat(result.b.breachedUnderwritingGuidelines).isNullOrEmpty()
    }
}
