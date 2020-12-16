package com.hedvig.underwriter.service

import arrow.core.Either
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.Market
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.DEBT_CHECK
import com.hedvig.underwriter.service.quoteStrategies.QuoteStrategyService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.testhelp.databuilder.NorwegianHomeContentDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.NorwegianTravelDataBuilder
import com.hedvig.underwriter.testhelp.databuilder.QuoteBuilder
import com.hedvig.underwriter.testhelp.databuilder.SwedishApartmentQuoteRequestBuilder
import com.hedvig.underwriter.testhelp.databuilder.SwedishHouseDataBuilder
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.javamoney.moneta.Money
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.util.UUID

@ExtendWith(MockKExtension::class)
class QuoteServiceImplTest {

    @MockK
    lateinit var underwriter: Underwriter

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var productPricingService: ProductPricingService

    @MockK
    lateinit var quoteRepository: QuoteRepository

    lateinit var cut: QuoteService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        cut = QuoteServiceImpl(
            underwriter,
            memberService,
            productPricingService,
            quoteRepository,
            mockk(),
            QuoteStrategyService(mockk(), productPricingService)
        )
        every { productPricingService.calculateInsuranceCost(Money.of(BigDecimal.TEN, "SEK"), "12345") } returns
            InsuranceCost(
                MonetaryAmountV2.of(BigDecimal.TEN, "SEK"),
                MonetaryAmountV2.of(BigDecimal.ZERO, "SEK"),
                MonetaryAmountV2.of(BigDecimal.TEN, "SEK"),
                null
            )
    }

    @Test
    fun calculateInsuranceCost() {
        val quote = QuoteBuilder(memberId = "12345", price = BigDecimal.TEN).build()

        val result = cut.calculateInsuranceCost(quote)

        verify(exactly = 1) { productPricingService.calculateInsuranceCost(Money.of(BigDecimal.TEN, "SEK"), "12345") }
    }

    @Test
    fun returnTheCorrectMarketForApartmentQuote() {
        val quote = QuoteBuilder(memberId = "12345", price = BigDecimal.TEN).build()
        every { cut.getLatestQuoteForMemberId(any()) } returns quote
        val result = cut.getMarketFromLatestQuote("12345")

        assertThat(result).isEqualTo(Market.SWEDEN)
    }

    @Test
    fun returnTheCorrectMarketForHouseQuote() {
        val quote =
            QuoteBuilder(memberId = "12345", price = BigDecimal.TEN, data = SwedishHouseDataBuilder()).build()
        every { cut.getLatestQuoteForMemberId(any()) } returns quote
        val result = cut.getMarketFromLatestQuote("12345")

        assertThat(result).isEqualTo(Market.SWEDEN)
    }

    @Test
    fun returnTheCorrectMarketForNorwegianHomeContent() {
        val quote =
            QuoteBuilder(memberId = "12345", price = BigDecimal.TEN, data = NorwegianHomeContentDataBuilder())
                .build()
        every { cut.getLatestQuoteForMemberId(any()) } returns quote
        val result = cut.getMarketFromLatestQuote("12345")

        assertThat(result).isEqualTo(Market.NORWAY)
    }

    @Test
    fun returnTheCorrectMarketForNorwegianTravel() {
        val quote =
            QuoteBuilder(memberId = "12345", price = BigDecimal.TEN, data = NorwegianTravelDataBuilder()).build()
        every { cut.getLatestQuoteForMemberId(any()) } returns quote
        val result = cut.getMarketFromLatestQuote("12345")

        assertThat(result).isEqualTo(Market.NORWAY)
    }

    @Test
    fun `createQuote insert quotes with breachedUnderwritingGuideline`() {
        every {
            underwriter.createQuote(any(), any(), any(), any())
        } returns Either.left(
            QuoteBuilder(breachedUnderwritingGuidelines = listOf(DEBT_CHECK)).build() to
                listOf(DEBT_CHECK)
        )

        val captureList = mutableListOf<Quote>()
        every {
            quoteRepository.insert(capture(captureList), any())
        } returns Unit

        val quoteRequestWithRandomUWGLBreach = SwedishApartmentQuoteRequestBuilder().build()

        val result = cut.createQuote(
            quoteRequestWithRandomUWGLBreach,
            UUID.randomUUID(),
            QuoteInitiatedFrom.WEBONBOARDING,
            null,
            false
        )

        require(result is Either.Left)
        assertThat(captureList[0].breachedUnderwritingGuidelines).isEqualTo(listOf(DEBT_CHECK))
    }
}
