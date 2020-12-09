package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.Market
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.BreachedGuideline
import com.hedvig.underwriter.service.quoteStrategies.QuoteStrategyService
import com.hedvig.underwriter.serviceIntegration.priceEngine.PriceEngineService
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryResponse
import com.hedvig.underwriter.testhelp.databuilder.a
import com.hedvig.underwriter.web.dtos.ErrorCodes
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.javamoney.moneta.Money
import org.junit.Test
import java.util.UUID

class CreateQuoteTest {
    val strategyService = mockk<QuoteStrategyService>()
    val priceEngineService = mockk<PriceEngineService>()
    val quoteRepository = mockk<QuoteRepository>()
    val metrics = mockk<Metrics>(relaxed = true)

    val cut = QuoteServiceImpl(
        UnderwriterImpl(priceEngineService, strategyService, metrics),
        mockk(),
        mockk(),
        quoteRepository,
        mockk(),
        strategyService
    )

    @Test
    fun successfully_create_quote() {

        val request = a.NorwegianHomeContentsQuoteRequestBuilder().build()

        every { strategyService.getAllGuidelines(any()) } returns setOf()
        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(1, "NOK")
        )
        every { quoteRepository.insert(any(), any()) } returns Unit

        val result = cut.createQuote(request, UUID.randomUUID(), QuoteInitiatedFrom.ANDROID, null, true)
        assert(result is Either.Right)
    }

    @Test
    fun fail_uw_guideline_returns_error_response_dto() {

        val request = a.NorwegianHomeContentsQuoteRequestBuilder().build()

        every { strategyService.getAllGuidelines(any()) } returns setOf(
            object : BaseGuideline<QuoteData> {
                override val breachedGuideline: BreachedGuideline
                    get() = BreachedGuideline("This guideline will always fail", "errorcode")
                override val validate: (QuoteData) -> Boolean
                    get() = { true }
            }
        )

        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(1, "NOK")
        )
        every { quoteRepository.insert(any(), any()) } returns Unit

        val result = cut.createQuote(request, UUID.randomUUID(), QuoteInitiatedFrom.ANDROID, null, true)
        require(result is Either.Left)
        assert(result.a.errorCode == ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES)
        verify { metrics.increment(Market.NORWAY, "errorcode") }
    }
}
