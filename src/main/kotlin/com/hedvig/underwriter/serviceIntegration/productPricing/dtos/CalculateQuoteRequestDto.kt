package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import java.time.LocalDate

data class CalculateQuoteRequestDto(
    val memberId: String,
    val ssn: String,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    val student: Boolean,
    val address: Address,
    val livingSpace: Float,
    val houseType: ProductPricingProductTypes,
    val currentInsurer: String?,
    val personsInHouseHold: Int,
    val ancillaryArea: Int?,
    val yearOfConstruction: Int?,
    val numberOfBathrooms: Int?,
    val extraBuildings: List<ExtraBuildingDTO>?,
    val isSubleted: Boolean?
)
