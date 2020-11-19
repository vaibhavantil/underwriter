package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers

import com.hedvig.productPricingObjects.dtos.ExtraBuildingDto
import com.hedvig.underwriter.model.ExtraBuildingType
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.ExtraBuildingRequestDto

class IncomingMapper {
    companion object {
        fun toExtraBuildingRequestDto(extraBuildingDto: ExtraBuildingDto) =
            ExtraBuildingRequestDto(
                id = null,
                type = ExtraBuildingType.valueOf(extraBuildingDto.type.name),
                area = extraBuildingDto.area,
                hasWaterConnected = extraBuildingDto.hasWaterConnected
            )
    }
}
