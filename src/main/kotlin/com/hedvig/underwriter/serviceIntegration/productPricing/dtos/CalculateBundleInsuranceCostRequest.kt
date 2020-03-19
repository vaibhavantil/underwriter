package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

data class CalculateBundleInsuranceCostRequest(
    val toBeBundled: List<CalculateBundledPriceDto>
)
