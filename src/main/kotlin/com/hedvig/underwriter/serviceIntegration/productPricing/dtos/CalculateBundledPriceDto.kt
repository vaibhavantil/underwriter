package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import javax.money.MonetaryAmount

data class CalculateBundledPriceDto(
    val grossPrice: MonetaryAmount,
    val insuranceType: InsuranceType
)
