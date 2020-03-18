package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundledPriceDto

data class CalculateBundleInsuranceCostRequest(
    val productsToBeBundled: List<CalculateBundledPriceDto>
)
