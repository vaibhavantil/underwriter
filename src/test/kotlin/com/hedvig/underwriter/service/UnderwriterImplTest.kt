package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.guidelines.AgeRestrictionGuideline
import com.hedvig.underwriter.service.guidelines.SwedishApartmentHouseHoldSizeAtLeast1
import com.hedvig.underwriter.service.guidelines.SwedishApartmentHouseHoldSizeNotMoreThan6
import com.hedvig.underwriter.service.guidelines.SwedishApartmentLivingSpaceAtLeast1Sqm
import com.hedvig.underwriter.service.guidelines.SwedishApartmentLivingSpaceNotMoreThan250Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseExtraBuildingsSizeAtLeast1Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseExtraBuildingsSizeNotOverThan75Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseHouseholdSizeAtLeast1
import com.hedvig.underwriter.service.guidelines.SwedishHouseHouseholdSizeNotMoreThan6
import com.hedvig.underwriter.service.guidelines.SwedishHouseLivingSpaceAtLeast1Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseLivingSpaceNotMoreThan250Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseNumberOfBathrooms
import com.hedvig.underwriter.service.guidelines.SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseYearOfConstruction
import com.hedvig.underwriter.service.guidelines.SwedishStudentApartmentAgeNotMoreThan30Years
import com.hedvig.underwriter.service.guidelines.SwedishStudentApartmentHouseholdSizeNotMoreThan2
import com.hedvig.underwriter.service.guidelines.SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm
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
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesSwedishStudentApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            ssn = "200112031356",
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                subType = ApartmentProductSubType.STUDENT_BRF,
                livingSpace = 50,
                householdSize = 2
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesSwedishHouseQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishHouseQuoteRequestBuilder().build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForHouseQuote(any()) } returns QuotePriceResponseDto(BigDecimal.ONE)

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesNorwegianHomeContentsQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder().build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForNorwegianHomeContentsQuote(any()) } returns QuotePriceResponseDto(BigDecimal.ONE)

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesNorwegianTravelQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.NorwegianTravelQuoteRequestBuilder().build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForNorwegianTravelQuote(any()) } returns QuotePriceResponseDto(BigDecimal.ONE)

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
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(listOf(AgeRestrictionGuideline.errorMessage))
    }

    @Test
    fun underwritingGuidelineHitAllLowerApartmentRulesOnCreatesSwedishApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                householdSize = 0,
                livingSpace = 0
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(
            listOf(
                SwedishApartmentHouseHoldSizeAtLeast1.errorMessage,
                SwedishApartmentLivingSpaceAtLeast1Sqm.errorMessage
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperApartmentRulesOnCreatesSwedishApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                householdSize = 7,
                livingSpace = 251
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(
            listOf(
                SwedishApartmentHouseHoldSizeNotMoreThan6.errorMessage,
                SwedishApartmentLivingSpaceNotMoreThan250Sqm.errorMessage
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllLowerStudentApartmentRulesOnCreatesSwedishStudentApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            ssn = "200112031356",
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                subType = ApartmentProductSubType.STUDENT_BRF,
                livingSpace = 0,
                householdSize = 0
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(
            listOf(
                SwedishApartmentHouseHoldSizeAtLeast1.errorMessage,
                SwedishApartmentLivingSpaceAtLeast1Sqm.errorMessage
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperStudentApartmentRulesOnCreatesSwedishStudentApartmentQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            ssn = "198812031356",
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                subType = ApartmentProductSubType.STUDENT_BRF,
                livingSpace = 51,
                householdSize = 3
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForApartmentQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(
            listOf(
                SwedishStudentApartmentHouseholdSizeNotMoreThan2.errorMessage,
                SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm.errorMessage,
                SwedishStudentApartmentAgeNotMoreThan30Years.errorMessage
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllLowerHouseRulesOnCreatesSwedishHouseQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishHouseQuoteRequestBuilder(
            data = a.SwedishHouseQuoteRequestDataBuilder(
                householdSize = 0, livingSpace = 0, yearOfConstruction = 1924, extraBuildings = listOf(
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 0).build()
                )
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForHouseQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(
            listOf(
                SwedishHouseHouseholdSizeAtLeast1.errorMessage,
                SwedishHouseLivingSpaceAtLeast1Sqm.errorMessage,
                SwedishHouseYearOfConstruction.errorMessage,
                SwedishHouseExtraBuildingsSizeAtLeast1Sqm.errorMessage
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperHouseRulesOnCreatesSwedishHouseQuote() {
        val debtChecker = mockk<DebtChecker>()
        val productPricingService = mockk<ProductPricingService>()

        val cut = UnderwriterImpl(debtChecker, productPricingService)
        val quoteRequest = a.SwedishHouseQuoteRequestBuilder(
            data = a.SwedishHouseQuoteRequestDataBuilder(
                householdSize = 7, livingSpace = 251, numberOfBathrooms = 3, extraBuildings = listOf(
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 7).build(),
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 7).build(),
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 7).build(),
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 7).build(),
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 76).build()
                )
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { productPricingService.priceFromProductPricingForHouseQuote(any()) } returns QuotePriceResponseDto(
            BigDecimal.ONE
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a).isEqualTo(
            listOf(
                SwedishHouseHouseholdSizeNotMoreThan6.errorMessage,
                SwedishHouseLivingSpaceNotMoreThan250Sqm.errorMessage,
                SwedishHouseNumberOfBathrooms.errorMessage,
                SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm.errorMessage,
                SwedishHouseExtraBuildingsSizeNotOverThan75Sqm.errorMessage
            )
        )
    }
}
