package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import org.javamoney.moneta.Money

data class CalculateInsuranceCostRequest(
    val price: Money
)
