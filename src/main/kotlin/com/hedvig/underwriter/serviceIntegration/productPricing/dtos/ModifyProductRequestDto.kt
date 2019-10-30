package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.HouseData
import com.hedvig.underwriter.model.Quote
import java.util.UUID

data class ModifyProductRequestDto(
    val idToBeReplaced: UUID,
    val memberId: String,
    val isStudent: Boolean,
    val street: String,
    val city: String?,
    val zipCode: String,
    val livingSpace: Float,
    val houseType: String,
    val personsInHouseHold: Int,
    val safetyIncreasers: List<String> = emptyList(),

    val ancillaryArea: Int? = null,
    val yearOfConstruction: Int? = null,
    val numberOfBathrooms: Int? = null,
    val extraBuildings: List<ExtraBuildingDto> = emptyList(),
    val isSubleted: Boolean = false,
    val floor: Int = 0
) {
    companion object {
        fun from(quote: Quote): ModifyProductRequestDto =
            when (quote.data) {
                is ApartmentData -> ModifyProductRequestDto(
                    idToBeReplaced = quote.originatingProductId
                        ?: throw IllegalArgumentException("Originating product id must be present to modify a product"),
                    memberId = quote.memberId
                        ?: throw IllegalArgumentException("memberId must be present in order to modify a product"),
                    isStudent = quote.data.isStudent,
                    street = quote.data.street!!,
                    city = quote.data.city,
                    zipCode = quote.data.zipCode!!,
                    livingSpace = quote.data.livingSpace!!.toFloat(),
                    houseType = quote.data.subType!!.toString(),
                    personsInHouseHold = quote.data.householdSize!!
                )
                is HouseData -> ModifyProductRequestDto(
                    idToBeReplaced = quote.originatingProductId
                        ?: throw IllegalArgumentException("Originating product id must be present to modify a product"),
                    memberId = quote.memberId
                        ?: throw IllegalArgumentException("memberId must be present in order to modify a product"),
                    isStudent = when (quote.data) {
                        is ApartmentData -> quote.data.isStudent
                        else -> false
                    },
                    street = quote.data.street!!,
                    city = quote.data.city,
                    zipCode = quote.data.zipCode!!,
                    livingSpace = quote.data.livingSpace!!.toFloat(),
                    houseType = "HOUSE",
                    personsInHouseHold = quote.data.householdSize!!,
                    ancillaryArea = quote.data.ancillaryArea,
                    yearOfConstruction = quote.data.yearOfConstruction,
                    numberOfBathrooms = quote.data.numberOfBathrooms,
                    extraBuildings = quote.data.extraBuildings.map { extraBuilding -> extraBuilding.toDto() },
                    isSubleted = quote.data.isSubleted!!,
                    floor = quote.data.floor
                )
            }
    }
}
