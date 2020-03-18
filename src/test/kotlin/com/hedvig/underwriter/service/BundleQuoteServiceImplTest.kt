package com.hedvig.underwriter.service

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.localization.service.LocalizationService
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.graphql.type.TypeMapper
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundleInsuranceCostRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundledPriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ProductType
import com.hedvig.underwriter.testhelp.databuilder.a
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal
import java.util.UUID
import org.javamoney.moneta.Money
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.util.Locale

@RunWith(MockitoJUnitRunner::class)
class BundleQuoteServiceImplTest {

    @MockK
    lateinit var quoteService: QuoteService

    @MockK
    lateinit var productPricingService: ProductPricingService

    @MockK
    lateinit var localizationService: LocalizationService

    lateinit var typeMapper: TypeMapper

    lateinit var cut: BundleQuotesServiceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        typeMapper = TypeMapper(localizationService)
        cut = BundleQuotesServiceImpl(quoteService, productPricingService, typeMapper)
    }

    @Test
    fun bundleNorwegianYouthQuotes() {
        val ids = listOf(UUID.randomUUID(), UUID.randomUUID())

        val quote1 = a.QuoteBuilder(
            id = ids[0],
            data = a.NorwegianHomeContentDataBuilder(isYouth = true),
            price = BigDecimal.TEN
        ).build()
        val quote2 = a.QuoteBuilder(
            id = ids[1],
            data = a.NorwegianTravelDataBuilder(isYouth = true),
            price = BigDecimal.TEN
        ).build()

        val requestCaptureMutableList = mutableListOf<CalculateBundleInsuranceCostRequest>()

        every {
            quoteService.getQuotes(ids)
        } returns listOf(quote1, quote2)

        every {
            productPricingService.calculateBundleInsuranceCost(
                capture(requestCaptureMutableList),
                any()
            )
        } returns InsuranceCost(
            MonetaryAmountV2.of(BigDecimal(20), "NOK"),
            MonetaryAmountV2.of(BigDecimal(0), "NOK"),
            MonetaryAmountV2.of(BigDecimal(20), "NOK"),
            null
        )

        every {
            localizationService.getText(any(), any())
        } returns ""

        cut.bundleQuotes("1337", ids, Locale("sv", "SE"))

        assertThat(requestCaptureMutableList.first()).isEqualTo(
            CalculateBundleInsuranceCostRequest(
                listOf(
                    CalculateBundledPriceDto(
                        Money.of(BigDecimal.TEN, "NOK"),
                        ProductType.NORWEGIAN_YOUTH_HOME_CONTENTS
                    ),
                    CalculateBundledPriceDto(
                        Money.of(BigDecimal.TEN, "NOK"),
                        ProductType.NORWEGIAN_YOUTH_TRAVEL
                    )
                )
            )
        )
    }

    @Test
    fun bundleNorwegianRegularQuotes() {
        val ids = listOf(UUID.randomUUID(), UUID.randomUUID())

        val quote1 = a.QuoteBuilder(
            id = ids[0],
            data = a.NorwegianHomeContentDataBuilder(),
            price = BigDecimal.TEN
        ).build()
        val quote2 = a.QuoteBuilder(
            id = ids[1],
            data = a.NorwegianTravelDataBuilder(),
            price = BigDecimal.TEN
        ).build()

        val requestCaptureMutableList = mutableListOf<CalculateBundleInsuranceCostRequest>()

        every {
            quoteService.getQuotes(ids)
        } returns listOf(quote1, quote2)

        every {
            productPricingService.calculateBundleInsuranceCost(
                capture(requestCaptureMutableList),
                any()
            )
        } returns InsuranceCost(
            MonetaryAmountV2.of(BigDecimal(20), "NOK"),
            MonetaryAmountV2.of(BigDecimal(0), "NOK"),
            MonetaryAmountV2.of(BigDecimal(20), "NOK"),
            null
        )

        every {
            localizationService.getText(any(), any())
        } returns ""

        cut.bundleQuotes("1337", ids, Locale("sv", "SE"))

        assertThat(requestCaptureMutableList.first()).isEqualTo(
            CalculateBundleInsuranceCostRequest(
                listOf(
                    CalculateBundledPriceDto(
                        Money.of(BigDecimal.TEN, "NOK"),
                        ProductType.NORWEGIAN_HOME_CONTENTS
                    ),
                    CalculateBundledPriceDto(
                        Money.of(BigDecimal.TEN, "NOK"),
                        ProductType.NORWEGIAN_TRAVEL
                    )
                )
            )
        )
    }

    @Test
    fun bundleSwedishStudentQuote() {
        val id = UUID.randomUUID()

        val quote = a.QuoteBuilder(
            id = id,
            data = a.ApartmentDataBuilder(subType = ApartmentProductSubType.STUDENT_RENT),
            price = BigDecimal.TEN
        ).build()

        val requestCaptureMutableList = mutableListOf<CalculateBundleInsuranceCostRequest>()

        every {
            quoteService.getQuotes(listOf(id))
        } returns listOf(quote)

        every {
            productPricingService.calculateBundleInsuranceCost(
                capture(requestCaptureMutableList),
                any()
            )
        } returns InsuranceCost(
            MonetaryAmountV2.of(BigDecimal(10), "SEK"),
            MonetaryAmountV2.of(BigDecimal(0), "SEK"),
            MonetaryAmountV2.of(BigDecimal(10), "SEK"),
            null
        )

        every {
            localizationService.getText(any(), any())
        } returns ""

        cut.bundleQuotes("1337", listOf(id), Locale("sv", "SE"))

        assertThat(requestCaptureMutableList.first()).isEqualTo(
            CalculateBundleInsuranceCostRequest(
                listOf(
                    CalculateBundledPriceDto(
                        Money.of(BigDecimal.TEN, "SEK"),
                        ProductType.SWEDISH_STUDENT_BRF
                    )
                )
            )
        )
    }

    @Test
    fun bundleSwedishRegularQuote() {
        val id = UUID.randomUUID()

        val quote = a.QuoteBuilder(
            id = id,
            data = a.ApartmentDataBuilder(),
            price = BigDecimal.TEN
        ).build()

        val requestCaptureMutableList = mutableListOf<CalculateBundleInsuranceCostRequest>()

        every {
            quoteService.getQuotes(listOf(id))
        } returns listOf(quote)

        every {
            productPricingService.calculateBundleInsuranceCost(
                capture(requestCaptureMutableList),
                any()
            )
        } returns InsuranceCost(
            MonetaryAmountV2.of(BigDecimal(10), "SEK"),
            MonetaryAmountV2.of(BigDecimal(0), "SEK"),
            MonetaryAmountV2.of(BigDecimal(10), "SEK"),
            null
        )

        every {
            localizationService.getText(any(), any())
        } returns ""

        cut.bundleQuotes("1337", listOf(id), Locale("sv", "SE"))

        assertThat(requestCaptureMutableList.first()).isEqualTo(
            CalculateBundleInsuranceCostRequest(
                listOf(
                    CalculateBundledPriceDto(
                        Money.of(BigDecimal.TEN, "SEK"),
                        ProductType.SWEDISH_BRF
                    )
                )
            )
        )
    }
}
