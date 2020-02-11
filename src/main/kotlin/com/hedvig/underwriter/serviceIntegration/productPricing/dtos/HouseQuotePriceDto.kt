package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import java.time.LocalDate
import java.time.Year

data class HouseQuotePriceDto(
    val birthDate: LocalDate,
    val livingSpace: Int,
    val houseHoldSize: Int,
    val zipCode: String,
    val ancillaryArea: Int,
    val yearOfConstruction: Year,
    val numberOfBathrooms: Int,
    val extraBuildings: List<ExtraBuildingRequestDto>,
    val isSubleted: Boolean
) {
    companion object {
        fun from(completeQuote: Quote): HouseQuotePriceDto {
            val completeQuoteData = completeQuote.data
            if (completeQuoteData is SwedishHouseData) {
                return HouseQuotePriceDto(
                    birthDate = completeQuoteData.ssn!!.birthDateFromSwedishSsn(),
                    livingSpace = completeQuoteData.livingSpace!!,
                    houseHoldSize = completeQuoteData.householdSize!!,
                    zipCode = completeQuoteData.zipCode!!,
                    ancillaryArea = completeQuoteData.ancillaryArea!!,
                    numberOfBathrooms = completeQuoteData.numberOfBathrooms!!,
                    yearOfConstruction = Year.of(completeQuoteData.yearOfConstruction!!),
                    extraBuildings = completeQuoteData.extraBuildings!!.map { extraBuilding ->
                        ExtraBuildingRequestDto(
                            id = null,
                            hasWaterConnected = extraBuilding.hasWaterConnected,
                            area = extraBuilding.area,
                            type = extraBuilding.type
                        )
                    },
                    isSubleted = completeQuoteData.isSubleted!!
                )
            }
            throw RuntimeException("missing data cannot create house quote price dto")
        }
    }
}
