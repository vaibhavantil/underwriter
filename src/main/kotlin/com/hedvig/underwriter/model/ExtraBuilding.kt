package com.hedvig.underwriter.model

import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto

data class ExtraBuilding(
    val type: ExtraBuildingType,
    val area: Int,
    val hasWaterConnected: Boolean,
    val displayName: String?
) {
    fun toDto(): ExtraBuildingRequestDto =
        ExtraBuildingRequestDto(
            id = null,
            type = type,
            area = area,
            hasWaterConnected = hasWaterConnected
        )

    companion object {
        fun from(extraBuildingDto: ExtraBuildingRequestDto): ExtraBuilding =
            ExtraBuilding(
                type = extraBuildingDto.type,
                area = extraBuildingDto.area,
                hasWaterConnected = extraBuildingDto.hasWaterConnected,
                displayName = null
            )
    }
}
