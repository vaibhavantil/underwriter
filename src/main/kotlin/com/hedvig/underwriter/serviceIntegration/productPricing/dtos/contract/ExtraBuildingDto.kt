package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.hedvig.underwriter.model.ExtraBuilding
import com.hedvig.underwriter.model.ExtraBuildingType

data class ExtraBuildingDto(
    val type: ExtraBuildingType,
    val area: Int,
    val hasWaterConnected: Boolean
) {
    companion object {
        fun from(extraBuilding: ExtraBuilding) = ExtraBuildingDto(
            type = extraBuilding.type,
            area = extraBuilding.area,
            hasWaterConnected = extraBuilding.hasWaterConnected
        )
    }
}
