package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import java.time.LocalDate
import java.time.Year

data class HouseQuotePriceDto (
    val birthDate: LocalDate,
    val livingSpace: Int,
    val houseHoldSize: Int,
    val zipCode: String,
    val ancillaryArea: Int,
    val yearOfConstruction: Year,
    val numberOfBathrooms: Int,
    val extraBuildings: List<ExtraBuildingDto>,
    val isSubleted: Boolean
)
