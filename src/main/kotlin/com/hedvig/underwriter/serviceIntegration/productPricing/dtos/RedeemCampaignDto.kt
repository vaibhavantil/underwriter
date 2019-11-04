package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import java.time.LocalDate

data class RedeemCampaignDto(
    val memberId: String,
    val code: String,
    val activationDate: LocalDate
)
