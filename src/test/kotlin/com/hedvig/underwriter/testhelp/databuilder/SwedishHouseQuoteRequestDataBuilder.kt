package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.ExtraBuildingRequestDto

data class SwedishHouseQuoteRequestDataBuilder(
    val street: String = "",
    val city: String = "",
    val zipCode: String = "",
    val householdSize: Int = 3,
    val livingSpace: Int = 2,
    val ancillaryArea: Int = 50,
    val yearOfConstruction: Int = 1925,
    val numberOfBathrooms: Int = 1,
    val extraBuildings: List<ExtraBuildingRequestDto> = emptyList(),
    val isSubleted: Boolean = false
) : DataBuilder<QuoteRequestData.SwedishHouse> {
    override fun build() = QuoteRequestData.SwedishHouse(
        street = street,
        zipCode = zipCode,
        city = city,
        livingSpace = livingSpace,
        householdSize = householdSize,
        ancillaryArea = ancillaryArea,
        yearOfConstruction = yearOfConstruction,
        numberOfBathrooms = numberOfBathrooms,
        extraBuildings = extraBuildings,
        isSubleted = isSubleted
    )
}
