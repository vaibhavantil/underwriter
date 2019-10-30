package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import java.util.UUID

data class ExtraBuildingDto(
    val id: UUID?,
    val type: String,
    val area: Int,
    val hasWaterConnected: Boolean,
    val displayName: String?
)
