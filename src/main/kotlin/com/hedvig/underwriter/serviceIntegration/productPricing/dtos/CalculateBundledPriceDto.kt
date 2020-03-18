package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import org.javamoney.moneta.Money

data class CalculateBundledPriceDto(
    val grossPrice: Money,
    val productType: ProductType
)
