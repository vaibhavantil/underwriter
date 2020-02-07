package com.hedvig.underwriter.graphql

import com.graphql.spring.boot.test.GraphQLTestTemplate
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.SignService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import java.math.BigDecimal
import java.time.LocalDate
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
    fun createSuccessfulQuote() {
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

        val response = graphQLTestTemplate.perform("/mutations/createQuote.graphql", null)
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
