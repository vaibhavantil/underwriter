package com.hedvig.underwriter.model

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

    fun getDefaultDisplayName(): String = when (this) {
        GARAGE -> "Garage"
        CARPORT -> "Carport"
        SHED -> "Skjul"
        STOREHOUSE -> "Förråd"
        FRIGGEBOD -> "Friggebod"
        ATTEFALL -> "Attefallshus"
        OUTHOUSE -> "Uthus"
        GUESTHOUSE -> "Gästhus"
        GAZEBO -> "Lusthus"
        GREENHOUSE -> "Växthus"
        SAUNA -> "Bastu"
        BARN -> "Lada"
        BOATHOUSE -> "Båthus"
        OTHER -> "Övrigt"
    }
}
