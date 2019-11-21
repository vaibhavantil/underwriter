package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ModifiedProductCreatedDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ModifyProductRequestDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioProductCreatedResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioQuoteRequestDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RedeemCampaignDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service

@Service
@EnableFeignClients
class ProductPricingServiceImpl @Autowired constructor(
    val productPricingClient: ProductPricingClient
) : ProductPricingService {
    override fun priceFromProductPricingForHouseQuote(houseQuotePriceDto: HouseQuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.priceFromProductPricingForHouseQuote(houseQuotePriceDto).body!!.price
        return QuotePriceResponseDto(price)
    }

    override fun priceFromProductPricingForApartmentQuote(apartmentQuotePriceDto: ApartmentQuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.priceFromProductPricingForHomeQuote(apartmentQuotePriceDto).body!!.price
        return QuotePriceResponseDto(price)
    }

    override fun createProduct(
        rapioQuoteRequest: RapioQuoteRequestDto,
        memberId: String
    ): RapioProductCreatedResponseDto {
        val rapioProductCreatedResponseDto = this.productPricingClient.createProduct(rapioQuoteRequest, memberId)
        val signedQuote = rapioProductCreatedResponseDto.body
        return signedQuote!!
    }

    override fun createModifiedProductFromQuote(quoteRequestDto: ModifyProductRequestDto): ModifiedProductCreatedDto =
        productPricingClient.createModifiedProductFromQuote(quoteRequestDto, quoteRequestDto.memberId)

    override fun redeemCampaign(redeemCampaignDto: RedeemCampaignDto) =
        this.productPricingClient.redeemCampaign(redeemCampaignDto)
}
