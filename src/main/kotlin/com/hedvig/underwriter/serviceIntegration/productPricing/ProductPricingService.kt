package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HomeQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioProductCreatedResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioQuoteRequestDto

interface ProductPricingService {
    fun priceFromProductPricingForHomeQuote(homeQuotePriceDto: HomeQuotePriceDto): QuotePriceResponseDto

    fun priceFromProductPricingForHouseQuote(houseQuotePriceDto: HouseQuotePriceDto): QuotePriceResponseDto

    fun createProduct(rapioQuoteRequest: RapioQuoteRequestDto, memberId: String): RapioProductCreatedResponseDto
}
