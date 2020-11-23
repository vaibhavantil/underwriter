package com.hedvig.underwriter.service.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.ExtraBuildingRequestDto

sealed class QuoteRequestData {
    data class SwedishHouse(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val livingSpace: Int?,
        val householdSize: Int?,
        val ancillaryArea: Int?,
        val yearOfConstruction: Int?,
        val numberOfBathrooms: Int?,
        val extraBuildings: List<ExtraBuildingRequestDto>?,
        @field:JsonProperty("subleted")
        val isSubleted: Boolean?,
        val floor: Int? = 0
    ) : QuoteRequestData()

    data class SwedishApartment(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val livingSpace: Int?,
        val householdSize: Int?,
        val floor: Int?,
        val subType: ApartmentProductSubType?
    ) : QuoteRequestData()

    data class NorwegianHomeContents(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val coInsured: Int?,
        val livingSpace: Int?,
        @field:JsonProperty("youth")
        val isYouth: Boolean?,
        val subType: NorwegianHomeContentsType?
    ) : QuoteRequestData()

    data class NorwegianTravel(
        val coInsured: Int?,
        @field:JsonProperty("youth")
        val isYouth: Boolean?
    ) : QuoteRequestData()

    data class DanishHomeContents(
        val street: String?,
        val zipCode: String?,
        val coInsured: Int?,
        val livingSpace: Int?,
        @field:JsonProperty("student")
        val isStudent: Boolean?,
        val subType: DanishHomeContentsType?
    ) : QuoteRequestData()

    data class DanishAccident(
        val street: String?,
        val zipCode: String?,
        val coInsured: Int?,
        @field:JsonProperty("student")
        val isStudent: Boolean?
    ) : QuoteRequestData()

    data class DanishTravel(
        val street: String?,
        val zipCode: String?,
        val coInsured: Int?,
        @field:JsonProperty("student")
        val isStudent: Boolean?
    ) : QuoteRequestData()

    companion object {
        fun from(quoteSchema: QuoteSchema) = when (quoteSchema) {
            is QuoteSchema.SwedishApartment -> SwedishApartment(
                subType = quoteSchema.lineOfBusiness,
                street = quoteSchema.street,
                zipCode = quoteSchema.zipCode,
                city = quoteSchema.city,
                livingSpace = quoteSchema.livingSpace,
                householdSize = quoteSchema.numberCoInsured + 1,
                floor = null
            )
            is QuoteSchema.SwedishHouse -> SwedishHouse(
                street = quoteSchema.street,
                zipCode = quoteSchema.zipCode,
                city = quoteSchema.city,
                livingSpace = quoteSchema.livingSpace,
                householdSize = quoteSchema.numberCoInsured + 1,
                ancillaryArea = quoteSchema.ancillaryArea,
                yearOfConstruction = quoteSchema.yearOfConstruction,
                numberOfBathrooms = quoteSchema.numberOfBathrooms,
                extraBuildings = quoteSchema.extraBuildings.map { extraBuildingSchema ->
                    ExtraBuildingRequestDto(
                        id = null,
                        type = extraBuildingSchema.type,
                        area = extraBuildingSchema.area,
                        hasWaterConnected = extraBuildingSchema.hasWaterConnected
                    )
                },
                isSubleted = quoteSchema.isSubleted,
                floor = null
            )
            is QuoteSchema.NorwegianHomeContent -> NorwegianHomeContents(
                subType = quoteSchema.lineOfBusiness,
                isYouth = quoteSchema.isYouth,
                street = quoteSchema.street,
                zipCode = quoteSchema.zipCode,
                city = quoteSchema.city,
                livingSpace = quoteSchema.livingSpace,
                coInsured = quoteSchema.numberCoInsured
            )
            is QuoteSchema.NorwegianTravel -> NorwegianTravel(
                isYouth = quoteSchema.isYouth,
                coInsured = quoteSchema.numberCoInsured
            )
            is QuoteSchema.DanishHomeContent -> DanishHomeContents(
                street = quoteSchema.street,
                zipCode = quoteSchema.zipCode,
                livingSpace = quoteSchema.livingSpace,
                coInsured = quoteSchema.numberCoInsured,
                isStudent = quoteSchema.isStudent,
                subType = quoteSchema.lineOfBusiness
            )
            is QuoteSchema.DanishAccident -> DanishAccident(
                street = quoteSchema.street,
                zipCode = quoteSchema.zipCode,
                coInsured = quoteSchema.numberCoInsured,
                isStudent = quoteSchema.isStudent
            )
            is QuoteSchema.DanishTravel -> DanishTravel(
                street = quoteSchema.street,
                zipCode = quoteSchema.zipCode,
                coInsured = quoteSchema.numberCoInsured,
                isStudent = quoteSchema.isStudent
            )
        }
    }
}
