package com.hedvig.underwriter.graphql.type

data class ExtraBuildingInput(
    val type: ExtraBuildingType,
    val area: Int,
    val hasWaterConnected: Boolean
)
