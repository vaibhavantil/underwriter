package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import java.math.BigDecimal
import java.time.LocalDate
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
    val activationDate: LocalDate,
    val previousInsuranceTerminationDate: LocalDate,
    val price: BigDecimal,

    val ancillaryArea: Int? = null,
    val yearOfConstruction: Int? = null,
    val numberOfBathrooms: Int? = null,
    val extraBuildings: List<ExtraBuildingRequestDto>? = emptyList(),
    @get:JvmName("getIsSubleted")
    val isSubleted: Boolean = false,
    val floor: Int = 0
) {
    companion object {
        fun from(
            quote: Quote,
            activationDate: LocalDate,
            previousInsuranceTerminationDate: LocalDate
        ): ModifyProductRequestDto =
            when (quote.data) {
                is SwedishApartmentData -> ModifyProductRequestDto(
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
                    personsInHouseHold = quote.data.householdSize!!,
                    activationDate = activationDate,
                    previousInsuranceTerminationDate = previousInsuranceTerminationDate,
                    price = quote.price!!
                )

                is SwedishHouseData -> ModifyProductRequestDto(
                    idToBeReplaced = quote.originatingProductId
                        ?: throw IllegalArgumentException("Originating product id must be present to modify a product"),
                    memberId = quote.memberId
                        ?: throw IllegalArgumentException("memberId must be present in order to modify a product"),
                    isStudent = when (quote.data) {
                        is SwedishApartmentData -> quote.data.isStudent
                        else -> false
                    },
                    street = quote.data.street!!,
                    city = quote.data.city,
                    zipCode = quote.data.zipCode!!,
                    livingSpace = quote.data.livingSpace!!.toFloat(),
                    houseType = "HOUSE",
                    personsInHouseHold = quote.data.householdSize!!,
                    price = quote.price!!,
                    activationDate = activationDate,
                    previousInsuranceTerminationDate = previousInsuranceTerminationDate,
                    ancillaryArea = quote.data.ancillaryArea,
                    yearOfConstruction = quote.data.yearOfConstruction,
                    numberOfBathrooms = quote.data.numberOfBathrooms,
                    extraBuildings = quote.data.extraBuildings?.map { extraBuilding -> extraBuilding.toDto() },
                    isSubleted = quote.data.isSubleted!!
                )
                is NorwegianHomeContentsData -> TODO()
                is NorwegianTravelData -> TODO()
            }
    }
}
