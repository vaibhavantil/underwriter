package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.*
import com.hedvig.underwriter.web.Dtos.SignedQuoteResponseDto
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid

interface ProductPricingService {
    fun priceFromProductPricingForHomeQuote(homeQuotePriceDto: HomeQuotePriceDto): QuotePriceResponseDto

    fun priceFromProductPricingForHouseQuote(houseQuotePriceDto: HouseQuotePriceDto): QuotePriceResponseDto

    fun createProduct(rapioQuoteRequest: RapioQuoteRequestDto, memberId: String): RapioProductCreatedResponseDto
}