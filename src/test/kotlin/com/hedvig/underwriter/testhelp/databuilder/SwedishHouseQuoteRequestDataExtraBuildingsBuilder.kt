package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ExtraBuildingType
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.ExtraBuildingRequestDto
import java.util.UUID

data class SwedishHouseQuoteRequestDataExtraBuildingsBuilder(
    val id: UUID? = UUID.randomUUID(),
    val type: ExtraBuildingType? = ExtraBuildingType.GARAGE,
    val area: Int? = 5,
    val hasWaterConnected: Boolean? = true
) : DataBuilder<ExtraBuildingRequestDto> {
    override fun build() =
        ExtraBuildingRequestDto(
            id = id,
            type = type!!,
            area = area!!,
            hasWaterConnected = hasWaterConnected!!
        )
}
