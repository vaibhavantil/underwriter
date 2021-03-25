package com.hedvig.underwriter.service

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.quoteStrategies.QuoteStrategyService
import com.hedvig.underwriter.testhelp.databuilder.QB
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test

class UnderwriterImplValidateAndCompleteQuoteTest {

    val quoteStrategyService: QuoteStrategyService = mockk()

    val cut = UnderwriterImpl(
        mockk(relaxed = true),
        quoteStrategyService,
        mockk(relaxed = true)
    )

    @Test
    fun `FailsShouldSkipAfter should only return error of FailsShouldSkipAfter`() {
        every { quoteStrategyService.getAllGuidelines(someQuote) } returns setOf(
            FailsShouldSkipAfter,
            FailsShouldNotSkipAfter
        )

        val result = cut.validateGuidelines(someQuote)

        assertThat(result).isEqualTo(listOf(FailsShouldSkipAfter.breachedGuideline))
    }

    @Test
    fun `SuccessShouldSkipAfter followed by FailsShouldNotSkipAfter and FailsShouldSkipAfter should return FailsShouldNotSkipAfter and FailsShouldSkipAfter`() {
        every { quoteStrategyService.getAllGuidelines(someQuote) } returns setOf(
            SuccessShouldSkipAfter,
            FailsShouldNotSkipAfter,
            FailsShouldSkipAfter
        )

        val result = cut.validateGuidelines(someQuote)

        assertThat(result).isEqualTo(
            listOf(
                FailsShouldNotSkipAfter.breachedGuideline,
                FailsShouldSkipAfter.breachedGuideline
            )
        )
    }

    @Test
    fun `SuccessShouldSkipAfter and SuccessShouldNotSkipAfter should return empty list`() {
        every { quoteStrategyService.getAllGuidelines(someQuote) } returns setOf(
            SuccessShouldSkipAfter,
            SuccessShouldNotSkipAfter
        )

        val result = cut.validateGuidelines(someQuote)

        assertThat(result).isEqualTo(emptyList())
    }

    @Test
    fun `two FailsShouldNotSkipAfter should return two FailsShouldNotSkipAfter`() {
        every { quoteStrategyService.getAllGuidelines(someQuote) } returns setOf(
            FailsShouldNotSkipAfter,
            FailsShouldSkipAfter
        )

        val result = cut.validateGuidelines(someQuote)

        assertThat(result).isEqualTo(
            listOf(
                FailsShouldNotSkipAfter.breachedGuideline,
                FailsShouldSkipAfter.breachedGuideline
            )
        )
    }

    companion object {
        val someQuote = QB().build()
    }
}

private object FailsShouldNotSkipAfter : BaseGuideline<QuoteData> {
    override val breachedGuideline = this::class.simpleName!!

    override val skipAfter: Boolean
        get() = false

    override val validate: (QuoteData) -> Boolean = { true }
}

private object FailsShouldSkipAfter : BaseGuideline<QuoteData> {
    override val breachedGuideline = this::class.simpleName!!

    override val skipAfter: Boolean
        get() = true

    override val validate: (QuoteData) -> Boolean = { true }
}

private object SuccessShouldSkipAfter : BaseGuideline<QuoteData> {
    override val breachedGuideline = this::class.simpleName!!

    override val skipAfter: Boolean
        get() = true

    override val validate: (QuoteData) -> Boolean = { false }
}

private object SuccessShouldNotSkipAfter : BaseGuideline<QuoteData> {
    override val breachedGuideline = this::class.simpleName!!

    override val skipAfter: Boolean
        get() = false

    override val validate: (QuoteData) -> Boolean = { false }
}
