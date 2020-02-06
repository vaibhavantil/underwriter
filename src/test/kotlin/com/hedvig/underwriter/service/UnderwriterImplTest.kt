package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.guidelines.AgeRestrictionGuideline
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.testhelp.databuilder.a
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UnderwriterImplTest {

    @Test
    fun successfullyCreatesSwedishApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder().build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(BigDecimal.ONE)

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun underwritingGuidelineHitAgeOnCreatesSwedishApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(ssn = "202001010000").build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(BigDecimal.ONE)

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(listOf(AgeRestrictionGuideline.errorMessage))
    }
}
