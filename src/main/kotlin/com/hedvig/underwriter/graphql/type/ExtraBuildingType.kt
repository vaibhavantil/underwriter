package com.hedvig.underwriter.graphql.type

enum class ExtraBuildingType {
    GARAGE,
    CARPORT,
    SHED,
    STOREHOUSE,
    FRIGGEBOD,
    ATTEFALL,
    OUTHOUSE,
    GUESTHOUSE,
    GAZEBO,
    GREENHOUSE,
    SAUNA,
    BARN,
    BOATHOUSE,
    OTHER;

    fun toExtraBuildingType() = when (this) {
        GARAGE -> com.hedvig.underwriter.model.ExtraBuildingType.GARAGE
        CARPORT -> com.hedvig.underwriter.model.ExtraBuildingType.CARPORT
        SHED -> com.hedvig.underwriter.model.ExtraBuildingType.SHED
        STOREHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.STOREHOUSE
        FRIGGEBOD -> com.hedvig.underwriter.model.ExtraBuildingType.FRIGGEBOD
        ATTEFALL -> com.hedvig.underwriter.model.ExtraBuildingType.ATTEFALL
        OUTHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.OUTHOUSE
        GUESTHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.GUESTHOUSE
        GAZEBO -> com.hedvig.underwriter.model.ExtraBuildingType.GAZEBO
        GREENHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.GREENHOUSE
        SAUNA -> com.hedvig.underwriter.model.ExtraBuildingType.SAUNA
        BARN -> com.hedvig.underwriter.model.ExtraBuildingType.BARN
        BOATHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.BOATHOUSE
        OTHER -> com.hedvig.underwriter.model.ExtraBuildingType.OTHER
    }
}
