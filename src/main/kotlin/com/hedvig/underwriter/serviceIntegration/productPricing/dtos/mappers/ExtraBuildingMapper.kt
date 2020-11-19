package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers

import com.hedvig.productPricingObjects.dtos.ExtraBuildingDto
import com.hedvig.productPricingObjects.enums.ExtraBuildingType
import com.hedvig.underwriter.model.ExtraBuilding

class ExtraBuildingMapper {
    companion object {
        fun toExtraBuildingDto(extraBuilding: ExtraBuilding) = ExtraBuildingDto(
            id = null,
            type = ExtraBuildingType.valueOf(extraBuilding.type.name),
            area = extraBuilding.area,
            hasWaterConnected = extraBuilding.hasWaterConnected,
            displayName = extraBuilding.displayName
        )
    }
}
