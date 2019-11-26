package com.hedvig.underwriter.graphql.type

data class ExtraBuilding(
    val type: ExtraBuildingType,
    val area: Int,
    val hasWaterConnected: Boolean,
    val displayName: String
)
