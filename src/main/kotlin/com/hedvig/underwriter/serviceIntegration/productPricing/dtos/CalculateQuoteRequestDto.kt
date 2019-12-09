package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.HouseData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.birthDateFromSsn
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
) {
    companion object {
        fun from(quote: Quote): CalculateQuoteRequestDto {
            return when (quote.data) {
                is ApartmentData -> CalculateQuoteRequestDto(
                    memberId = quote.memberId!!,
                    ssn = quote.data.ssn!!,
                    firstName = quote.data.firstName!!,
                    lastName = quote.data.lastName!!,
                    birthDate = quote.data.ssn!!.birthDateFromSsn(),
                    student = quote.data.isStudent,
                    address = Address(
                        quote.data.street!!,
                        quote.data.city,
                        quote.data.zipCode!!,
                        0
                    ),
                    livingSpace = quote.data.livingSpace!!.toFloat(),
                    houseType = when (quote.data.subType!!) {
                        ApartmentProductSubType.BRF -> ProductPricingProductTypes.BRF
                        ApartmentProductSubType.RENT -> ProductPricingProductTypes.RENT
                        ApartmentProductSubType.RENT_BRF -> TODO() // what is even rent brf?
                        ApartmentProductSubType.SUBLET_RENTAL -> ProductPricingProductTypes.STUDENT_RENT
                        ApartmentProductSubType.SUBLET_BRF -> ProductPricingProductTypes.STUDENT_BRF
                        ApartmentProductSubType.STUDENT_BRF -> ProductPricingProductTypes.STUDENT_BRF
                        ApartmentProductSubType.STUDENT_RENT -> ProductPricingProductTypes.STUDENT_RENT
                        ApartmentProductSubType.LODGER -> TODO()
                        ApartmentProductSubType.UNKNOWN -> TODO()
                    },
                    currentInsurer = quote.currentInsurer,
                    personsInHouseHold = quote.data.householdSize!!,
                    ancillaryArea = null,
                    yearOfConstruction = null,
                    numberOfBathrooms = null,
                    extraBuildings = null,
                    isSubleted = null
                )
                is HouseData -> CalculateQuoteRequestDto(
                    memberId = quote.memberId!!,
                    ssn = quote.data.ssn!!,
                    firstName = quote.data.firstName!!,
                    lastName = quote.data.lastName!!,
                    birthDate = quote.data.ssn!!.birthDateFromSsn(),
                    student = false,
                    address = Address(
                        quote.data.street!!,
                        quote.data.city,
                        quote.data.zipCode!!,
                        0
                    ),
                    livingSpace = quote.data.livingSpace!!.toFloat(),
                    houseType = ProductPricingProductTypes.HOUSE,
                    currentInsurer = quote.currentInsurer,
                    personsInHouseHold = quote.data.householdSize!!,
                    ancillaryArea = quote.data.ancillaryArea,
                    yearOfConstruction = quote.data.yearOfConstruction,
                    numberOfBathrooms = quote.data.numberOfBathrooms,
                    extraBuildings = quote.data.extraBuildings?.map { extraBuilding ->
                        ExtraBuildingDTO(
                            type = com.hedvig.underwriter.graphql.type.ExtraBuildingType.valueOf(extraBuilding.type.name),
                            area = extraBuilding.area,
                            hasWaterConnected = extraBuilding.hasWaterConnected
                        )
                    },
                    isSubleted = quote.data.isSubleted
                )
            }
        }
    }
}
