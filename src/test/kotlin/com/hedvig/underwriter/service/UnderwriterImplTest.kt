package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import io.mockk.every
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID


@RunWith(MockitoJUnitRunner::class)
class UnderwriterImplTest {

    @Test
    fun successfullyChecksUnderwritingGuidelines() {
        /*val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            state = QuoteState.INCOMPLETE,
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.HEDVIG,
            data = SwedishApartmentData(
                firstName = "Sherlock",
                lastName = "Holmes",
                ssn = "199003041234",
                street = "221 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "London",
                id = UUID.randomUUID(),
                subType = ApartmentProductSubType.BRF
            ),
            currentInsurer = null,
            memberId = "123456",
            breachedUnderwritingGuidelines = null
        )

        every { debtChecker.passesDebtCheck(any()) } returns listOf("fails debt check")

        val result = quote.complete(debtChecker, productPricingService)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(listOf("fails debt check"))*/
    }


    @Test
    fun successfullyBypassesUnderwritingGuidelines() {
        /*val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            state = QuoteState.INCOMPLETE,
            initiatedFrom = QuoteInitiatedFrom.APP,
            attributedTo = Partner.HEDVIG,
            data = SwedishApartmentData(
                firstName = "Sherlock",
                lastName = "Holmes",
                ssn = "199003041234",
                street = "221 Baker street",
                zipCode = "11216",
                livingSpace = 33,
                householdSize = 4,
                city = "London",
                id = UUID.randomUUID(),
                subType = ApartmentProductSubType.BRF
            ),
            currentInsurer = null,
            memberId = "123456",
            breachedUnderwritingGuidelines = null
        )

        val breachedUnderwritingGuidelines = listOf("fails debt check")
        val bypasser = "blargh@hedvig.com"
        every { debtChecker.passesDebtCheck(any()) } returns breachedUnderwritingGuidelines
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns
            QuotePriceResponseDto(BigDecimal.valueOf(100))

        val result = quote.complete(debtChecker, productPricingService, bypasser)
        require(result is Either.Right)
        assertThat(result.b.breachedUnderwritingGuidelines).isEqualTo(breachedUnderwritingGuidelines)
        assertThat(result.b.underwritingGuidelinesBypassedBy).isEqualTo(bypasser)*/
    }
}
