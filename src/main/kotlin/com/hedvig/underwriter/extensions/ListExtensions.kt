package com.hedvig.underwriter.extensions

import com.hedvig.underwriter.graphql.type.ExtraBuildingInput
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto

fun List<ExtraBuildingInput>.toExtraBuilding(): List<ExtraBuildingRequestDto> = this.map { extraBuildingInput ->
    ExtraBuildingRequestDto(
        id = null,
        type = extraBuildingInput.type.toExtraBuildingType(),
        area = extraBuildingInput.area,
        hasWaterConnected = extraBuildingInput.hasWaterConnected
    )
}
