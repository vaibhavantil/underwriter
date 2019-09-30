package com.hedvig.underwriter.serviceIntegration.productPricing

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import javax.validation.Valid


@Headers("Accept: application/json;charset=utf-8")
@FeignClient(
        name = "productPricingClient",
        url = "\${hedvig.product-pricing.url:product-pricing}")
interface ProductPricingClient {

    @PostMapping("/insurance/getQuotePrice")
    fun quotePrice(@Valid @RequestBody req: QuotePriceDto): ResponseEntity<QuotePriceResponseDto>

    @PostMapping("/insurance/createProduct")
    fun createProduct()
}
