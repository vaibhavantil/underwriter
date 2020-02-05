package com.hedvig.underwriter.model

import arrow.core.Either
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData.Apartment
import com.hedvig.underwriter.service.model.QuoteRequestData.House
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import io.mockk.every
import io.mockk.mockk
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class QuoteTest {
    @Test
    fun updatesQuote() {
        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            data = SwedishApartmentData(id = UUID.randomUUID()),
            productType = ProductType.APARTMENT,
            initiatedFrom = QuoteInitiatedFrom.HOPE,
            attributedTo = Partner.HEDVIG,
            state = QuoteState.QUOTED,
            breachedUnderwritingGuidelines = null,
            price = BigDecimal.valueOf(100)
        )
        val updatedQuote = quote.update(
            QuoteRequest(
                firstName = null,
                lastName = null,
                email = null,
                productType = null,
                ssn = "201212121212",
                currentInsurer = null,
                incompleteQuoteData = null,
                originatingProductId = null,
                quotingPartner = null,
                birthDate = null,
                memberId = null,
                startDate = null,
                dataCollectionId = null
            )
        )
        assertThat(updatedQuote.id).isEqualTo(quote.id)
        assertThat((updatedQuote.data as SwedishApartmentData).ssn).isEqualTo("201212121212")
    }

    @Test
    fun updatesQuoteFromApartmentToHouse() {
        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            data = SwedishApartmentData(
                id = UUID.randomUUID(),
                ssn = "201212121212",
                street = "Storgatan 1"
            ),
            productType = ProductType.APARTMENT,
            initiatedFrom = QuoteInitiatedFrom.HOPE,
            attributedTo = Partner.HEDVIG,
            state = QuoteState.QUOTED,
            breachedUnderwritingGuidelines = null,
            price = BigDecimal.valueOf(100)
        )
        val updatedQuote = quote.update(
            QuoteRequest(
                firstName = null,
                lastName = null,
                email = null,
                productType = ProductType.HOUSE,
                ssn = "201212121213",
                currentInsurer = null,
                incompleteQuoteData = House(
                    street = "Storgatan 2",
                    zipCode = null,
                    city = null,
                    livingSpace = null,
                    householdSize = null,
                    isSubleted = null,
                    yearOfConstruction = null,
                    numberOfBathrooms = null,
                    ancillaryArea = null,
                    extraBuildings = null
                ),
                originatingProductId = null,
                quotingPartner = null,
                birthDate = null,
                memberId = null,
                startDate = null,
                dataCollectionId = null
            )
        )
        assertThat(updatedQuote.id).isEqualTo(quote.id)
        assertThat(updatedQuote.productType).isEqualTo(ProductType.HOUSE)
        assertThat((updatedQuote.data as SwedishHouseData).ssn).isEqualTo("201212121213")
        assertThat((updatedQuote.data as SwedishHouseData).street).isEqualTo("Storgatan 2")
    }

    @Test
    fun updatesQuoteFromHouseToApartment() {
        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            data = SwedishHouseData(
                id = UUID.randomUUID(),
                ssn = "201212121212",
                street = "Storgatan 1"
            ),
            productType = ProductType.HOUSE,
            initiatedFrom = QuoteInitiatedFrom.HOPE,
            attributedTo = Partner.HEDVIG,
            breachedUnderwritingGuidelines = null,
            state = QuoteState.QUOTED,
            price = BigDecimal.valueOf(100)
        )
        val updatedQuote = quote.update(
            QuoteRequest(
                firstName = null,
                lastName = null,
                email = null,
                productType = ProductType.APARTMENT,
                ssn = "201212121213",
                currentInsurer = null,
                incompleteQuoteData = Apartment(
                    street = "Storgatan 2",
                    zipCode = null,
                    city = null,
                    livingSpace = null,
                    householdSize = null,
                    subType = ApartmentProductSubType.BRF,
                    floor = null
                ),
                originatingProductId = null,
                quotingPartner = null,
                birthDate = null,
                memberId = null,
                startDate = null,
                dataCollectionId = null
            )
        )
        assertThat(updatedQuote.id).isEqualTo(quote.id)
        assertThat(updatedQuote.productType).isEqualTo(ProductType.APARTMENT)
        assertThat((updatedQuote.data as SwedishApartmentData).ssn).isEqualTo("201212121213")
        assertThat((updatedQuote.data as SwedishApartmentData).street).isEqualTo("Storgatan 2")
        assertThat((updatedQuote.data as SwedishApartmentData).subType).isEqualTo(ApartmentProductSubType.BRF)
    }

    @Test
    fun successfullyChecksUnderwritingGuidelines() {
        val debtChecker = mockk<DebtChecker>()
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
        assertThat(result.a).isEqualTo(listOf("fails debt check"))
    }

    @Test
    fun successfullyBypassesUnderwritingGuidelines() {
        val debtChecker = mockk<DebtChecker>()
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
        assertThat(result.b.underwritingGuidelinesBypassedBy).isEqualTo(bypasser)
    }
}
