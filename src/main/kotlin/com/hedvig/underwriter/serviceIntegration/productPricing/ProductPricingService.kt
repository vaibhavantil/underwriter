package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.Dtos.QuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.Dtos.QuotePriceResponseDto

interface ProductPricingService {
    fun getQuotePrice(quotePriceDto: QuotePriceDto): QuotePriceResponseDto
}