package com.hedvig.underwriter.model

import com.hedvig.service.LocalizationService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto
import java.util.Locale

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

    fun toGraphQLResponseObject(localizationService: LocalizationService, locale: Locale) =
        when (type) {
            ExtraBuildingType.GARAGE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGarage(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.CARPORT -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingCarport(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.SHED -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingShed(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.STOREHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingStorehouse(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.FRIGGEBOD -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingFriggebod(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.ATTEFALL -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingAttefall(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.OUTHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingOuthouse(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.GUESTHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGuesthouse(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.GAZEBO -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGazebo(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.GREENHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGreenhouse(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.SAUNA -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingSauna(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.BARN -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingBarn(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.BOATHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingBoathouse(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
            ExtraBuildingType.OTHER -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingOther(
                area = area,
                hasWaterConnected = hasWaterConnected,
                displayName = type.getDisplayName(localizationService, locale)
            )
        }

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
