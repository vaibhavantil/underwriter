package com.hedvig.underwriter.serviceIntegration.productPricing

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.*
import com.hedvig.underwriter.web.Dtos.ErrorQuoteResponseDto
import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service

@Service
@EnableFeignClients
class ProductPricingServiceImpl @Autowired constructor(
        val productPricingClient: ProductPricingClient
): ProductPricingService {

    override fun priceFromProductPricingForHouseQuote(houseQuotePriceDto: HouseQuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.priceFromProductPricingForHouseQuote(houseQuotePriceDto).body?.price
        return QuotePriceResponseDto(price)
    }

    override fun priceFromProductPricingForHomeQuote(homeQuotePriceDto: HomeQuotePriceDto): QuotePriceResponseDto {
        val price = this.productPricingClient.priceFromProductPricingForHomeQuote(homeQuotePriceDto).body?.price
        return QuotePriceResponseDto(price)
    }

    override fun createProduct(rapioQuoteRequestDto: RapioQuoteRequestDto, memberId: String): RapioProductCreatedResponseDto {
        val rapioProductCreatedResponseDto = this.productPricingClient.createProduct(rapioQuoteRequestDto, memberId)
        val signedQuote = rapioProductCreatedResponseDto.body
        return signedQuote!!
    }
}

