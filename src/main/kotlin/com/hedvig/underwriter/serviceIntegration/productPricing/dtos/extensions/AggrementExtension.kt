package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.extensions

import com.hedvig.productPricingObjects.dtos.Agreement
import com.hedvig.productPricingObjects.enums.DanishAccidentLineOfBusiness
import com.hedvig.productPricingObjects.enums.DanishHomeContentLineOfBusiness
import com.hedvig.productPricingObjects.enums.DanishTravelLineOfBusiness
import com.hedvig.productPricingObjects.enums.NorwegianHomeContentLineOfBusiness
import com.hedvig.productPricingObjects.enums.NorwegianTravelLineOfBusiness
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers.IncomingMapper

fun Agreement.getOldProductType() = when (this) {
    is Agreement.SwedishApartment -> ProductType.APARTMENT
    is Agreement.SwedishHouse -> ProductType.HOUSE
    is Agreement.NorwegianHomeContent, is Agreement.DanishHomeContent -> ProductType.HOME_CONTENT
    is Agreement.NorwegianTravel, is Agreement.DanishTravel -> ProductType.TRAVEL
    is Agreement.DanishAccident -> ProductType.ACCIDENT
}

fun Agreement.toQuoteRequestData() = when (this) {
    is Agreement.SwedishApartment -> QuoteRequestData.SwedishApartment(
        street = this.address.street,
        zipCode = this.address.postalCode,
        city = this.address.city,
        livingSpace = this.squareMeters.toInt(),
        householdSize = this.numberCoInsured + 1,
        subType = ApartmentProductSubType.valueOf(this.lineOfBusiness.name),
        floor = null
    )
    is Agreement.SwedishHouse -> QuoteRequestData.SwedishHouse(
        street = this.address.street,
        zipCode = this.address.postalCode,
        city = this.address.city,
        livingSpace = this.squareMeters.toInt(),
        householdSize = this.numberCoInsured + 1,
        ancillaryArea = this.ancillaryArea.toInt(),
        yearOfConstruction = this.yearOfConstruction,
        numberOfBathrooms = this.numberOfBathrooms,
        extraBuildings = this.extraBuildings.map { extraBuildingDto ->
            IncomingMapper.toExtraBuildingRequestDto(extraBuildingDto)
        },
        isSubleted = this.isSubleted,
        floor = 0
    )
    is Agreement.NorwegianHomeContent -> QuoteRequestData.NorwegianHomeContents(
        street = this.address.street,
        zipCode = this.address.postalCode,
        livingSpace = this.squareMeters.toInt(),
        city = this.address.city,
        coInsured = this.numberCoInsured,
        subType = when (this.lineOfBusiness) {
            NorwegianHomeContentLineOfBusiness.OWN,
            NorwegianHomeContentLineOfBusiness.YOUTH_OWN -> {
                NorwegianHomeContentsType.OWN
            }
            NorwegianHomeContentLineOfBusiness.RENT,
            NorwegianHomeContentLineOfBusiness.YOUTH_RENT -> {
                NorwegianHomeContentsType.RENT
            }
        },
        isYouth = when (this.lineOfBusiness) {
            NorwegianHomeContentLineOfBusiness.OWN,
            NorwegianHomeContentLineOfBusiness.RENT -> {
                false
            }
            NorwegianHomeContentLineOfBusiness.YOUTH_OWN,
            NorwegianHomeContentLineOfBusiness.YOUTH_RENT -> {
                true
            }
        }
    )
    is Agreement.NorwegianTravel -> QuoteRequestData.NorwegianTravel(
        coInsured = this.numberCoInsured,
        isYouth = when (this.lineOfBusiness) {
            NorwegianTravelLineOfBusiness.REGULAR -> false
            NorwegianTravelLineOfBusiness.YOUTH -> true
        }
    )
    is Agreement.DanishHomeContent -> QuoteRequestData.DanishHomeContents(
        street = this.address.street,
        zipCode = this.address.postalCode,
        city = this.address.city,
        apartment = this.address.apartment,
        floor = this.address.floor,
        bbrId = null,
        coInsured = this.numberCoInsured,
        livingSpace = this.squareMeters.toInt(),
        isStudent = when (this.lineOfBusiness) {
            DanishHomeContentLineOfBusiness.RENT, DanishHomeContentLineOfBusiness.OWN -> false
            DanishHomeContentLineOfBusiness.STUDENT_RENT, DanishHomeContentLineOfBusiness.STUDENT_OWN -> true
        },
        subType = when (this.lineOfBusiness) {
            DanishHomeContentLineOfBusiness.RENT, DanishHomeContentLineOfBusiness.STUDENT_RENT -> DanishHomeContentsType.RENT
            DanishHomeContentLineOfBusiness.OWN, DanishHomeContentLineOfBusiness.STUDENT_OWN -> DanishHomeContentsType.OWN
        }
    )
    is Agreement.DanishAccident -> QuoteRequestData.DanishAccident(
        street = this.address.street,
        zipCode = this.address.postalCode,
        city = this.address.city,
        apartment = this.address.apartment,
        floor = this.address.floor,
        bbrId = null,
        coInsured = this.numberCoInsured,
        isStudent = when (this.lineOfBusiness) {
            DanishAccidentLineOfBusiness.REGULAR -> false
            DanishAccidentLineOfBusiness.STUDENT -> true
        }
    )
    is Agreement.DanishTravel -> QuoteRequestData.DanishTravel(
        street = this.address.street,
        zipCode = this.address.postalCode,
        city = this.address.city,
        apartment = this.address.apartment,
        floor = this.address.floor,
        bbrId = null,
        coInsured = this.numberCoInsured,
        isStudent = when (this.lineOfBusiness) {
            DanishTravelLineOfBusiness.REGULAR -> false
            DanishTravelLineOfBusiness.STUDENT -> true
        }
    )
}
