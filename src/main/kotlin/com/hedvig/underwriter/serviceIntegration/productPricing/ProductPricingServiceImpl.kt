package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.Dtos.QuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.Dtos.QuotePriceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service

@Service
@EnableFeignClients
class ProductPricingServiceImpl @Autowired constructor(val productPricingClient: ProductPricingClient): ProductPricingService {

    override fun getQuotePrice(quotePriceDto: QuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.getQuotePrice(quotePriceDto).body?.price
        return QuotePriceResponseDto(price)
    }
}