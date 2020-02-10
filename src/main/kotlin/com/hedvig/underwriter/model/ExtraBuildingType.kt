package com.hedvig.underwriter.model

import com.hedvig.service.LocalizationService
import java.util.Locale

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

    fun getDisplayName(localizationService: LocalizationService, locale: Locale): String =
        localizationService.getText(locale, "EXTRA_BUILDING_DISPLAY_NAME_${this.name}") ?: getDefaultDisplayName(this)

    private fun getDefaultDisplayName(type: ExtraBuildingType): String = when (type) {
        ExtraBuildingType.GARAGE -> "Garage"
        ExtraBuildingType.CARPORT -> "Carport"
        ExtraBuildingType.SHED -> "Skjul"
        ExtraBuildingType.STOREHOUSE -> "Förråd"
        ExtraBuildingType.FRIGGEBOD -> "Friggebod"
        ExtraBuildingType.ATTEFALL -> "Attefallshus"
        ExtraBuildingType.OUTHOUSE -> "Uthus"
        ExtraBuildingType.GUESTHOUSE -> "Gästhus"
        ExtraBuildingType.GAZEBO -> "Lusthus"
        ExtraBuildingType.GREENHOUSE -> "Växthus"
        ExtraBuildingType.SAUNA -> "Bastu"
        ExtraBuildingType.BARN -> "Lada"
        ExtraBuildingType.BOATHOUSE -> "Båthus"
        ExtraBuildingType.OTHER -> "Övrigt"
    }
}
