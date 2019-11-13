package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import java.util.UUID

data class ExtraBuildingRequestDto(
    val id: UUID?,
    val type: String,
    val area: Int,
    val hasWaterConnected: Boolean
)
