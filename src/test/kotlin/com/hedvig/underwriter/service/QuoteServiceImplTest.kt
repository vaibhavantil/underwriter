package com.hedvig.underwriter.service

import arrow.core.Either
import arrow.core.Right
import arrow.core.getOrElse
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.serviceIntegration.customerio.CustomerIO
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ModifiedProductCreatedDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.SignedProductResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.SignedQuoteRequest
import com.hedvig.underwriter.testhelp.databuilder.a
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity

@RunWith(MockitoJUnitRunner::class)
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

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        cut = QuoteServiceImpl(underwriter, memberService, productPricingService, quoteRepository)
    }

    @Test
    fun activatesQuoteByCreatingModifiedProduct() {

        val quote = a.QuoteBuilder(originatingProductId = UUID.randomUUID(), memberId = "12345").build()

        val createdProductResponse = ModifiedProductCreatedDto(id = UUID.randomUUID())

        every { quoteRepository.find(quote.id) } returns quote
        val signedQuote = quote.copy(
            signedProductId = createdProductResponse.id,
            state = QuoteState.SIGNED
        )
        every {
            quoteRepository.update(
                match { passedQuote -> passedQuote.id == quote.id },
                any()
            )
        } returns signedQuote
        every { productPricingService.createModifiedProductFromQuote(any()) } returns createdProductResponse

        val result = cut.activateQuote(
            completeQuoteId = quote.id,
            activationDate = LocalDate.now().plusDays(10)
        )

        assertThat(result).isInstanceOf(Either.Right::class.java)
        assertThat(result.getOrElse { null }).isEqualTo(signedQuote)
        verify(exactly = 1) { productPricingService.createModifiedProductFromQuote(any()) } // FIXME better assertion?
    }

    @Test
    fun calculateInsuranceCost() {
        val service = QuoteServiceImpl(
            underwriter = underwriter,
            memberService = memberService,
            productPricingService = productPricingService,
            quoteRepository = quoteRepository
        )

        every { productPricingService.calculateInsuranceCost(Money.of(BigDecimal.TEN, "SEK"), "12345") } returns
            InsuranceCost(
                MonetaryAmountV2.of(BigDecimal.TEN, "SEK"),
                MonetaryAmountV2.of(BigDecimal.ZERO, "SEK"),
                MonetaryAmountV2.of(BigDecimal.TEN, "SEK"),
                null
            )

        val quote = a.QuoteBuilder(memberId = "12345", price = BigDecimal.TEN).build()

        val result = service.calculateInsuranceCost(quote)

        verify(exactly = 1) { productPricingService.calculateInsuranceCost(Money.of(BigDecimal.TEN, "SEK"), "12345") }
    }
}
