package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.graphql.type.ExtraBuildingType

data class ExtraBuildingDTO(
    val type: ExtraBuildingType,
    val area: Int,
    val hasWaterConnected: Boolean
)
