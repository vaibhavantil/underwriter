package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HomeQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service

@Service
@EnableFeignClients
class ProductPricingServiceImpl @Autowired constructor(val productPricingClient: ProductPricingClient): ProductPricingService {
    override fun priceFromProductPricingForHouseQuote(houseQuotePriceDto: HouseQuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.priceFromProductPricingForHouseQuote(houseQuotePriceDto).body?.price
        return QuotePriceResponseDto(price)
    }

    override fun priceFromProductPricingForHomeQuote(homeQuotePriceDto: HomeQuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.priceFromProductPricingForHomeQuote(homeQuotePriceDto).body?.price
        return QuotePriceResponseDto(price)
    }

    override fun createProduct() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}