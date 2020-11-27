package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.guidelines.AgeRestrictionGuideline
import com.hedvig.underwriter.service.guidelines.NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm
import com.hedvig.underwriter.service.guidelines.NorwegianHomeContentscoInsuredNotMoreThan5
import com.hedvig.underwriter.service.guidelines.NorwegianSsnNotMatchesBirthDate
import com.hedvig.underwriter.service.guidelines.NorwegianTravelCoInsuredNotMoreThan5
import com.hedvig.underwriter.service.guidelines.NorwegianYouthHomeContentsAgeNotMoreThan30Years
import com.hedvig.underwriter.service.guidelines.NorwegianYouthHomeContentsCoInsuredNotMoreThan0
import com.hedvig.underwriter.service.guidelines.NorwegianYouthHomeContentsLivingSpaceNotMoreThan50Sqm
import com.hedvig.underwriter.service.guidelines.NorwegianYouthTravelAgeNotMoreThan30Years
import com.hedvig.underwriter.service.guidelines.NorwegianYouthTravelCoInsuredNotMoreThan0
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
import com.hedvig.underwriter.serviceIntegration.priceEngine.PriceEngineService
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryResponse
import com.hedvig.underwriter.testhelp.databuilder.a
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.assertj.core.api.Assertions.assertThat
import org.javamoney.moneta.Money
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@RunWith(SpringRunner::class)
class UnderwriterImplTest {

    @MockkBean
    lateinit var debtChecker: DebtChecker

    @MockkBean
    lateinit var priceEngineService: PriceEngineService

    @Before
    fun setup() {
        every {
            priceEngineService.querySwedishApartmentPrice(
                any()
            )
        } returns
            PriceQueryResponse(
                UUID.randomUUID(),
                Money.of(BigDecimal.ONE, "SEK")
            )

        every {
            priceEngineService.querySwedishHousePrice(
                any()
            )
        } returns
            PriceQueryResponse(
                UUID.randomUUID(),
                Money.of(BigDecimal.ONE, "SEK")
            )
    }

    @Test
    fun successfullyCreatesSwedishApartmentQuote() {

        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder().build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesSwedishStudentApartmentQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            ssn = "200112031356",
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                subType = ApartmentProductSubType.STUDENT_BRF,
                livingSpace = 50,
                householdSize = 2
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesSwedishHouseQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishHouseQuoteRequestBuilder().build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesNorwegianHomeContentsQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder().build()
        val quoteId = UUID.randomUUID()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            quoteId,
            Money.of(BigDecimal.ONE, "NOK")
        )

        val result = cut.createQuote(quoteRequest, quoteId, QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreatesNorwegianTravelQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianTravelQuoteRequestBuilder().build()
        val quoteId = UUID.randomUUID()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        every { priceEngineService.queryNorwegianTravelPrice(any()) } returns PriceQueryResponse(
            quoteId,
            Money.of(BigDecimal.ONE, "NOK")
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun underwritingGuidelineHitAgeOnCreatesSwedishApartmentQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(ssn = "202001010000").build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(listOf(AgeRestrictionGuideline.breachedGuideline))
    }

    @Test
    fun underwritingGuidelineHitAllLowerApartmentRulesOnCreatesSwedishApartmentQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                householdSize = 0,
                livingSpace = 0
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                SwedishApartmentHouseHoldSizeAtLeast1.breachedGuideline,
                SwedishApartmentLivingSpaceAtLeast1Sqm.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperApartmentRulesOnCreatesSwedishApartmentQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                householdSize = 7,
                livingSpace = 251
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                SwedishApartmentHouseHoldSizeNotMoreThan6.breachedGuideline,
                SwedishApartmentLivingSpaceNotMoreThan250Sqm.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllLowerStudentApartmentRulesOnCreatesSwedishStudentApartmentQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            ssn = "200112031356",
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                subType = ApartmentProductSubType.STUDENT_BRF,
                livingSpace = 0,
                householdSize = 0
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                SwedishApartmentHouseHoldSizeAtLeast1.breachedGuideline,
                SwedishApartmentLivingSpaceAtLeast1Sqm.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperStudentApartmentRulesOnCreatesSwedishStudentApartmentQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishApartmentQuoteRequestBuilder(
            ssn = "198812031356",
            data = a.SwedishApartmentQuoteRequestDataBuilder(
                subType = ApartmentProductSubType.STUDENT_BRF,
                livingSpace = 51,
                householdSize = 3
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                SwedishStudentApartmentHouseholdSizeNotMoreThan2.breachedGuideline,
                SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm.breachedGuideline,
                SwedishStudentApartmentAgeNotMoreThan30Years.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllLowerHouseRulesOnCreatesSwedishHouseQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.SwedishHouseQuoteRequestBuilder(
            data = a.SwedishHouseQuoteRequestDataBuilder(
                householdSize = 0, livingSpace = 0, yearOfConstruction = 1924, extraBuildings = listOf(
                    a.SwedishHouseQuoteRequestDataExtraBuildingsBuilder(area = 0).build()
                )
            )
        ).build()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                SwedishHouseHouseholdSizeAtLeast1.breachedGuideline,
                SwedishHouseLivingSpaceAtLeast1Sqm.breachedGuideline,
                SwedishHouseYearOfConstruction.breachedGuideline,
                SwedishHouseExtraBuildingsSizeAtLeast1Sqm.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperHouseRulesOnCreatesSwedishHouseQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
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

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                SwedishHouseHouseholdSizeNotMoreThan6.breachedGuideline,
                SwedishHouseLivingSpaceNotMoreThan250Sqm.breachedGuideline,
                SwedishHouseNumberOfBathrooms.breachedGuideline,
                SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm.breachedGuideline,
                SwedishHouseExtraBuildingsSizeNotOverThan75Sqm.breachedGuideline
            )
        )
    }

    @Test
    fun successfullyCreateNorwegianHomeContentsQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder().build()

        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(0, "NOK")
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun underwritingGuidelineHitWhenNorwegianSsnNotMatch() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder(
            ssn = "24057408215"
        ).build()

        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(0, "NOK")
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                NorwegianSsnNotMatchesBirthDate.breachedGuideline
            )
        )
    }

    @Test
    fun successfullyCreateNorwegianHomeContentsQuoteWhenSsnIsNull() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder(
            ssn = null
        ).build()

        every { priceEngineService.queryNorwegianHomeContentPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(0, "NOK")
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun successfullyCreateNorwegianHomeTravelQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianTravelQuoteRequestBuilder().build()

        every { priceEngineService.queryNorwegianTravelPrice(any()) } returns PriceQueryResponse(
            UUID.randomUUID(),
            Money.of(0, "NOK")
        )

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }

    @Test
    fun underwritingGuidelineHitAllUpperApartmentRulesOnCreatesNorwegianHomeContentsQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder(
            data = a.NorwegianHomeContentsQuoteRequestDataBuilder(
                coInsured = 6,
                livingSpace = 251
            )
        ).build()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                NorwegianHomeContentscoInsuredNotMoreThan5.breachedGuideline,
                NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperApartmentRulesOnCreatesNorwegianHomeContentsYouthQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianHomeContentsQuoteRequestBuilder(
            ssn = "28026400734",
            birthDate = LocalDate.of(1964, 2, 28),
            data = a.NorwegianHomeContentsQuoteRequestDataBuilder(
                coInsured = 3,
                livingSpace = 51,
                isYouth = true
            )
        ).build()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                NorwegianYouthHomeContentsLivingSpaceNotMoreThan50Sqm.breachedGuideline,
                NorwegianYouthHomeContentsAgeNotMoreThan30Years.breachedGuideline,
                NorwegianYouthHomeContentsCoInsuredNotMoreThan0.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperApartmentRulesOnCreatesNorwegianTravelQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianTravelQuoteRequestBuilder(
            data = a.NorwegianTravelQuoteRequestDataBuilder(
                coInsured = 6
            )
        ).build()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                NorwegianTravelCoInsuredNotMoreThan5.breachedGuideline
            )
        )
    }

    @Test
    fun underwritingGuidelineHitAllUpperApartmentRulesOnCreatesNorwegianTravelYouthQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.NorwegianTravelQuoteRequestBuilder(
            birthDate = LocalDate.now().minusYears(31).minusDays(1),
            data = a.NorwegianTravelQuoteRequestDataBuilder(
                coInsured = 1,
                isYouth = true
            )
        ).build()

        val result = cut.createQuote(quoteRequest, UUID.randomUUID(), QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Left)
        assertThat(result.a.second).isEqualTo(
            listOf(
                NorwegianSsnNotMatchesBirthDate.breachedGuideline,
                NorwegianYouthTravelAgeNotMoreThan30Years.breachedGuideline,
                NorwegianYouthTravelCoInsuredNotMoreThan0.breachedGuideline
            )
        )
    }

    @Test
    fun successfullyCreatesDanishHomeContentsQuote() {
        val cut = UnderwriterImpl(debtChecker, priceEngineService)
        val quoteRequest = a.DanishHomeContentsQuoteRequestBuilder().build()
        val quoteId = UUID.randomUUID()

        every { debtChecker.passesDebtCheck(any()) } returns listOf()
        /* TODO: This should be verified once price engine is in place
        every { priceEngineService.queryDanishHomeContentPrice(any()) } returns PriceQueryResponse(
            quoteId,
            Money.of(BigDecimal.ONE, "NOK")
        )*/

        val result = cut.createQuote(quoteRequest, quoteId, QuoteInitiatedFrom.WEBONBOARDING, null)
        require(result is Either.Right)
    }
}
