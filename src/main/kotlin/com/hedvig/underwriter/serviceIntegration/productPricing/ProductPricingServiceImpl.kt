package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateInsuranceCostRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ModifiedProductCreatedDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ModifyProductRequestDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.NorwegianHomeContentsQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.NorwegianTravelQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RedeemCampaignDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.SignedQuoteRequest
import java.lang.RuntimeException
import org.javamoney.moneta.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@EnableFeignClients
class ProductPricingServiceImpl @Autowired constructor(
    val productPricingClient: ProductPricingClient
) : ProductPricingService {

    override fun priceFromProductPricingForHouseQuote(houseQuotePriceDto: HouseQuotePriceDto): QuotePriceResponseDto {
        val price = BigDecimal.ZERO //this.productPricingClient.priceFromProductPricingForHouseQuote(houseQuotePriceDto).body!!.price
        return QuotePriceResponseDto(price)
    }

    override fun priceFromProductPricingForApartmentQuote(apartmentQuotePriceDto: ApartmentQuotePriceDto): QuotePriceResponseDto {
        val price = BigDecimal.ZERO //this.productPricingClient.priceFromProductPricingForHomeQuote(apartmentQuotePriceDto).body!!.price
        return QuotePriceResponseDto(price)
    }

    override fun priceFromProductPricingForNorwegianHomeContentsQuote(norwegianHomeContentsQuotePriceDto: NorwegianHomeContentsQuotePriceDto): QuotePriceResponseDto {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun priceFromProductPricingForNorwegianTravelQuote(norwegianTravelQuotePriceDto: NorwegianTravelQuotePriceDto): QuotePriceResponseDto {
        TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun signedQuote(
        signedQuoteRequest: SignedQuoteRequest,
        memberId: String
    ) = productPricingClient.signedQuote(signedQuoteRequest, memberId).body
        ?: throw RuntimeException("Create product returned with empty body")

    override fun createModifiedProductFromQuote(quoteRequestDto: ModifyProductRequestDto): ModifiedProductCreatedDto =
        productPricingClient.createModifiedProductFromQuote(quoteRequestDto, quoteRequestDto.memberId)

    override fun redeemCampaign(redeemCampaignDto: RedeemCampaignDto) =
        this.productPricingClient.redeemCampaign(redeemCampaignDto)

    override fun calculateInsuranceCost(price: Money, memberId: String): InsuranceCost =
        productPricingClient.calculateInsuranceCost(CalculateInsuranceCostRequest(price), memberId).body!!
}
