package com.hedvig.underwriter.graphql

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.SignService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.NorwegianHomeContentsQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.NorwegianTravelQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Year
import org.javamoney.moneta.Money
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class GraphQlMutationsTest {

    @Autowired
    private lateinit var graphQLTestTemplate: GraphQLTestTemplate

    @MockBean
    lateinit var memberService: MemberService

    @MockBean
    lateinit var depthChecker: DebtChecker

    @MockBean
    lateinit var productPricingService: ProductPricingService

    @MockBean
    lateinit var signService: SignService

    @Test
    fun createSuccessfulOldApartmentQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForApartmentQuote(
                ApartmentQuotePriceDto(
                    birthDate = LocalDate.of(1912, 12, 12),
                    livingSpace = 30,
                    zipCode = "12345",
                    houseHoldSize = 2,
                    houseType = ApartmentProductSubType.BRF,
                    isStudent = false
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )
        Mockito.`when`(
            productPricingService.calculateInsuranceCost(
                Money.of(BigDecimal.ONE, "SEK"), "123"
            )
        ).thenReturn(
            InsuranceCost(
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                null
            )
        )

        graphQLTestTemplate.addHeader("hedvig.token", "123")

        val response = graphQLTestTemplate.perform("/mutations/createApartmentQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["id"].textValue() == "00000000-0000-0000-0000-000000000000")
        assert(createQuote["insuranceCost"]["monthlyGross"]["amount"].textValue() == "1.00")
        assert(createQuote["insuranceCost"]["monthlyGross"]["currency"].textValue() == "SEK")
        assert(createQuote["details"]["street"].textValue() == "Kungsgatan 1")
        assert(createQuote["details"]["zipCode"].textValue() == "12345")
        assert(createQuote["details"]["livingSpace"].intValue() == 30)
        assert(createQuote["details"]["householdSize"].intValue() == 2)
        assert(createQuote["details"]["type"].textValue() == "BRF")
    }

    @Test
    fun createSuccessfulOldHouseQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForHouseQuote(
                HouseQuotePriceDto(
                    birthDate = LocalDate.of(1912, 12, 12),
                    livingSpace = 30,
                    zipCode = "12345",
                    houseHoldSize = 2,
                    ancillaryArea = 100,
                    yearOfConstruction = Year.of(1925),
                    numberOfBathrooms = 1,
                    extraBuildings = emptyList(),
                    isSubleted = false
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )
        Mockito.`when`(
            productPricingService.calculateInsuranceCost(
                Money.of(BigDecimal.ONE, "SEK"), "123"
            )
        ).thenReturn(
            InsuranceCost(
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                null
            )
        )

        graphQLTestTemplate.addHeader("hedvig.token", "123")

        val response = graphQLTestTemplate.perform("/mutations/createHouseQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["id"].textValue() == "00000000-0000-0000-0000-000000000003")
        assert(createQuote["insuranceCost"]["monthlyGross"]["amount"].textValue() == "1.00")
        assert(createQuote["insuranceCost"]["monthlyGross"]["currency"].textValue() == "SEK")
        assert(createQuote["details"]["street"].textValue() == "Kungsgatan 1")
        assert(createQuote["details"]["zipCode"].textValue() == "12345")
        assert(createQuote["details"]["livingSpace"].intValue() == 30)
        assert(createQuote["details"]["householdSize"].intValue() == 2)
    }

    @Test
    fun createSuccessfulSwedishApartmentQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForApartmentQuote(
                ApartmentQuotePriceDto(
                    birthDate = LocalDate.of(1912, 12, 12),
                    livingSpace = 30,
                    zipCode = "12345",
                    houseHoldSize = 2,
                    houseType = ApartmentProductSubType.BRF,
                    isStudent = false
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )
        Mockito.`when`(
            productPricingService.calculateInsuranceCost(
                Money.of(BigDecimal.ONE, "SEK"), "123"
            )
        ).thenReturn(
            InsuranceCost(
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                null
            )
        )

        graphQLTestTemplate.addHeader("hedvig.token", "123")

        val response = graphQLTestTemplate.perform("/mutations/createSwedishApartmentQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["id"].textValue() == "00000000-0000-0000-0000-000000000004")
        assert(createQuote["insuranceCost"]["monthlyGross"]["amount"].textValue() == "1.00")
        assert(createQuote["insuranceCost"]["monthlyGross"]["currency"].textValue() == "SEK")
        assert(createQuote["quoteDetails"]["street"].textValue() == "Kungsgatan 1")
        assert(createQuote["quoteDetails"]["zipCode"].textValue() == "12345")
        assert(createQuote["quoteDetails"]["livingSpace"].intValue() == 30)
        assert(createQuote["quoteDetails"]["householdSize"].intValue() == 2)
        assert(createQuote["quoteDetails"]["apartmentType"].textValue() == "BRF")
    }

    @Test
    fun createSuccessfulSwedishHouseQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForHouseQuote(
                HouseQuotePriceDto(
                    birthDate = LocalDate.of(1912, 12, 12),
                    livingSpace = 30,
                    zipCode = "12345",
                    houseHoldSize = 2,
                    ancillaryArea = 100,
                    yearOfConstruction = Year.of(1925),
                    numberOfBathrooms = 1,
                    extraBuildings = emptyList(),
                    isSubleted = false
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )
        Mockito.`when`(
            productPricingService.calculateInsuranceCost(
                Money.of(BigDecimal.ONE, "SEK"), "123"
            )
        ).thenReturn(
            InsuranceCost(
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "SEK"),
                null
            )
        )

        graphQLTestTemplate.addHeader("hedvig.token", "123")

        val response = graphQLTestTemplate.perform("/mutations/createSwedishHouseQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["id"].textValue() == "00000000-0000-0000-0000-000000000005")
        assert(createQuote["insuranceCost"]["monthlyGross"]["amount"].textValue() == "1.00")
        assert(createQuote["insuranceCost"]["monthlyGross"]["currency"].textValue() == "SEK")
        assert(createQuote["quoteDetails"]["street"].textValue() == "Kungsgatan 1")
        assert(createQuote["quoteDetails"]["zipCode"].textValue() == "12345")
        assert(createQuote["quoteDetails"]["livingSpace"].intValue() == 30)
        assert(createQuote["quoteDetails"]["householdSize"].intValue() == 2)
    }

    @Test
    fun createSuccessfulNorwegianHomeContentsQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForNorwegianHomeContentsQuote(
                NorwegianHomeContentsQuotePriceDto(
                    birthDate = LocalDate.of(1912, 12, 12),
                    livingSpace = 30,
                    zipCode = "12345",
                    coInsured = 0,
                    type = NorwegianHomeContentsType.OWN,
                    isStudent = false
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )
        Mockito.`when`(
            productPricingService.calculateInsuranceCost(
                Money.of(BigDecimal.ONE, "NOK"), "123"
            )
        ).thenReturn(
            InsuranceCost(
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "NOK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "NOK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "NOK"),
                null
            )
        )

        graphQLTestTemplate.addHeader("hedvig.token", "123")

        val response = graphQLTestTemplate.perform("/mutations/createNorwegianHomeContentsQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["id"].textValue() == "00000000-0000-0000-0000-000000000006")
        assert(createQuote["insuranceCost"]["monthlyGross"]["amount"].textValue() == "1.00")
        assert(createQuote["insuranceCost"]["monthlyGross"]["currency"].textValue() == "NOK")
        assert(createQuote["quoteDetails"]["street"].textValue() == "Kungsgatan 2")
        assert(createQuote["quoteDetails"]["zipCode"].textValue() == "12345")
        assert(createQuote["quoteDetails"]["livingSpace"].intValue() == 30)
        assert(createQuote["quoteDetails"]["coInsured"].intValue() == 0)
        assert(createQuote["quoteDetails"]["norwegianType"].textValue() == "OWN")
    }

    @Test
    fun createSuccessfulNorwegianTravelQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForNorwegianTravelQuote(
                NorwegianTravelQuotePriceDto(
                    coInsured = 0
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )
        Mockito.`when`(
            productPricingService.calculateInsuranceCost(
                Money.of(BigDecimal.ONE, "NOK"), "123"
            )
        ).thenReturn(
            InsuranceCost(
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "NOK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "NOK"),
                MonetaryAmountV2.Companion.of(BigDecimal.ONE, "NOK"),
                null
            )
        )

        graphQLTestTemplate.addHeader("hedvig.token", "123")

        val response = graphQLTestTemplate.perform("/mutations/createNorwegianTravelQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["id"].textValue() == "00000000-0000-0000-0000-000000000007")
        assert(createQuote["insuranceCost"]["monthlyGross"]["amount"].textValue() == "1.00")
        assert(createQuote["insuranceCost"]["monthlyGross"]["currency"].textValue() == "NOK")
        assert(createQuote["quoteDetails"]["coInsured"].intValue() == 0)
    }

    @Test
    fun createUnderwritingLimitsHitQuote() {
        Mockito.`when`(
            productPricingService.priceFromProductPricingForApartmentQuote(
                ApartmentQuotePriceDto(
                    birthDate = LocalDate.of(1912, 12, 12),
                    livingSpace = 999,
                    zipCode = "12345",
                    houseHoldSize = 2,
                    houseType = ApartmentProductSubType.BRF,
                    isStudent = false
                )
            )
        ).thenReturn(
            QuotePriceResponseDto(
                BigDecimal.ONE
            )
        )

        val response = graphQLTestTemplate.perform("/mutations/createUnderwritingLimitHitQuote.graphql", null)
        val createQuote = response.readTree()["data"]["createQuote"]

        assert(response.isOk)
        assert(createQuote["limits"][0]["description"].textValue() != null)
    }
}
