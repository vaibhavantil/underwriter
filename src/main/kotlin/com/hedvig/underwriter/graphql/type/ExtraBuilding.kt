package com.hedvig.underwriter.graphql.type

sealed class ExtraBuilding {

    interface ExtraBuildingCore {
        val area: Int
        val displayName: String
        val hasWaterConnected: Boolean
    }

    data class ExtraBuildingGarage(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingCarport(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingShed(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingStorehouse(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingFriggebod(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingAttefall(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingOuthouse(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingGuesthouse(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingGazebo(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingGreenhouse(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingSauna(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingBarn(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingBoathouse(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore

    data class ExtraBuildingOther(
        override val area: Int,
        override val displayName: String,
        override val hasWaterConnected: Boolean
    ) : ExtraBuildingCore
}
