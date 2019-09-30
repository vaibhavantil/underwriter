package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto

interface ProductPricingService {
    fun quotePrice(quotePriceDto: QuotePriceDto): QuotePriceResponseDto

    fun createProduct()
}