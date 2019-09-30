package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service

@Service
@EnableFeignClients
class ProductPricingServiceImpl @Autowired constructor(val productPricingClient: ProductPricingClient): ProductPricingService {

    override fun quotePrice(quotePriceDto: QuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.quotePrice(quotePriceDto).body?.price
        return QuotePriceResponseDto(price)
    }

    override fun createProduct() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}