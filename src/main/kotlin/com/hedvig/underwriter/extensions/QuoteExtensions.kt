package com.hedvig.underwriter.extensions

import com.hedvig.service.LocalizationService
import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.graphql.type.ExtraBuilding
import com.hedvig.underwriter.graphql.type.IncompleteQuoteDetails
import com.hedvig.underwriter.graphql.type.NorwegianHomeContentsType
import com.hedvig.underwriter.graphql.type.QuoteDetails
import com.hedvig.underwriter.graphql.type.depricated.CompleteQuoteDetails
import com.hedvig.underwriter.model.ExtraBuilding as ExtraBuildingModel
import com.hedvig.underwriter.model.ExtraBuildingType
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import java.lang.IllegalStateException
import java.util.Locale

val Quote.firstName
    get() = (data as? PersonPolicyHolder<*>)?.firstName
        ?: throw RuntimeException("No firstName on Quote! $this")

val Quote.lastName
    get() = (data as? PersonPolicyHolder<*>)?.lastName
        ?: throw RuntimeException("No lastName on Quote! $this")

val Quote.ssn
    get() = (data as? PersonPolicyHolder<*>)?.ssn
        ?: throw RuntimeException("No ssn on Quote! $this")

val Quote.email
    get() = (data as? PersonPolicyHolder<*>)?.email

val Quote.swedishApartment
    get() = (data as? SwedishApartmentData)

val Quote.swedishHouse
    get() = (data as? SwedishHouseData)

val Quote.norwegianHomeContents
    get() = (data as? NorwegianHomeContentsData)

val Quote.norwegianTravel
    get() = (data as? NorwegianTravelData)

val Quote.validTo
    get() = this.createdAt.plusSeconds(this.validity)!!

fun Quote.createCompleteQuoteResult(
    localizationService: LocalizationService,
    locale: Locale
): CompleteQuoteDetails =
    this.swedishApartment?.let { apartment ->
        CompleteQuoteDetails.CompleteApartmentQuoteDetails(
            street = apartment.street!!,
            zipCode = apartment.zipCode!!,
            householdSize = apartment.householdSize!!,
            livingSpace = apartment.livingSpace!!,
            type = ApartmentType.valueOf(apartment.subType!!.name)
        )
    } ?: this.swedishHouse?.let { house ->
        CompleteQuoteDetails.CompleteHouseQuoteDetails(
            street = house.street!!,
            zipCode = house.zipCode!!,
            householdSize = house.householdSize!!,
            livingSpace = house.livingSpace!!,
            ancillarySpace = house.ancillaryArea!!,
            extraBuildings = house.extraBuildings!!.map { extraBuildingInput ->
                extraBuildingInput.toGraphQLResponseObject(localizationService, locale)
            },
            numberOfBathrooms = house.numberOfBathrooms!!,
            yearOfConstruction = house.yearOfConstruction!!,
            isSubleted = house.isSubleted!!
        )
    } ?: this.norwegianHomeContents?.let {
        CompleteQuoteDetails.UnknownQuoteDetails()
    } ?: this.norwegianTravel?.let {
        CompleteQuoteDetails.UnknownQuoteDetails()
    } ?: throw IllegalStateException("Trying to create QuoteDetails without `swedishApartment`, `swedishHouse` data")

fun Quote.createQuoteDetails(
    localizationService: LocalizationService,
    locale: Locale
): QuoteDetails =
    this.swedishApartment?.let { apartment ->
        QuoteDetails.SwedishApartmentQuoteDetails(
            street = apartment.street!!,
            zipCode = apartment.zipCode!!,
            householdSize = apartment.householdSize!!,
            livingSpace = apartment.livingSpace!!,
            type = ApartmentType.valueOf(apartment.subType!!.name)
        )
    } ?: this.swedishHouse?.let { house ->
        QuoteDetails.SwedishHouseQuoteDetails(
            street = house.street!!,
            zipCode = house.zipCode!!,
            householdSize = house.householdSize!!,
            livingSpace = house.livingSpace!!,
            ancillarySpace = house.ancillaryArea!!,
            extraBuildings = house.extraBuildings!!.map { extraBuildingInput ->
                extraBuildingInput.toGraphQLResponseObject(localizationService, locale)
            },
            numberOfBathrooms = house.numberOfBathrooms!!,
            yearOfConstruction = house.yearOfConstruction!!,
            isSubleted = house.isSubleted!!
        )
    } ?: this.norwegianHomeContents?.let {
        QuoteDetails.NorwegianHomeContentsDetails(
            street = it.street,
            zipCode = it.zipCode,
            coinsured = it.coinsured,
            livingSpace = it.livingSpace,
            isStudent = it.isStudent,
            type = NorwegianHomeContentsType.valueOf(it.type.name)
        )
    } ?: this.norwegianTravel?.let {
        QuoteDetails.NorwegianTravelDetails(
            coinsured = it.coinsured
        )
    } ?: throw IllegalStateException("Trying to create QuoteDetails without `swedishApartment`, `swedishHouse`, `norwegianHomeContents` or `norwegianTravel` data")

fun Quote.createIncompleteQuoteResult(
    localizationService: LocalizationService,
    locale: Locale
): IncompleteQuoteDetails? =
    this.swedishApartment?.let { apartment ->
        IncompleteQuoteDetails.IncompleteApartmentQuoteDetails(
            street = apartment.street,
            zipCode = apartment.zipCode,
            householdSize = apartment.householdSize,
            livingSpace = apartment.livingSpace,
            type = apartment.subType?.let { ApartmentType.valueOf(it.name) }
        )
    } ?: this.swedishHouse?.let { house ->
        IncompleteQuoteDetails.IncompleteHouseQuoteDetails(
            street = house.street,
            zipCode = house.zipCode,
            householdSize = house.householdSize,
            livingSpace = house.livingSpace,
            ancillarySpace = house.ancillaryArea,
            extraBuildings = house.extraBuildings?.map { extraBuildingInput ->
                extraBuildingInput.toGraphQLResponseObject(localizationService, locale)
            },
            numberOfBathrooms = house.numberOfBathrooms,
            yearOfConstruction = house.yearOfConstruction,
            isSubleted = house.isSubleted
        )
    }

private fun ExtraBuildingModel.toGraphQLResponseObject(localizationService: LocalizationService, locale: Locale) =
    when (type) {
        ExtraBuildingType.GARAGE -> ExtraBuilding.ExtraBuildingGarage(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.CARPORT -> ExtraBuilding.ExtraBuildingCarport(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.SHED -> ExtraBuilding.ExtraBuildingShed(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.STOREHOUSE -> ExtraBuilding.ExtraBuildingStorehouse(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.FRIGGEBOD -> ExtraBuilding.ExtraBuildingFriggebod(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.ATTEFALL -> ExtraBuilding.ExtraBuildingAttefall(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.OUTHOUSE -> ExtraBuilding.ExtraBuildingOuthouse(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.GUESTHOUSE -> ExtraBuilding.ExtraBuildingGuesthouse(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.GAZEBO -> ExtraBuilding.ExtraBuildingGazebo(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.GREENHOUSE -> ExtraBuilding.ExtraBuildingGreenhouse(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.SAUNA -> ExtraBuilding.ExtraBuildingSauna(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.BARN -> ExtraBuilding.ExtraBuildingBarn(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.BOATHOUSE -> ExtraBuilding.ExtraBuildingBoathouse(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
        ExtraBuildingType.OTHER -> ExtraBuilding.ExtraBuildingOther(
            area = area,
            hasWaterConnected = hasWaterConnected,
            displayName = type.getDisplayName(localizationService, locale)
        )
    }

private fun ExtraBuildingType.getDisplayName(localizationService: LocalizationService, locale: Locale): String =
    localizationService.getText(locale, "EXTRA_BUILDING_DISPLAY_NAME_${this.name}") ?: getDefaultDisplayName(this)

private fun getDefaultDisplayName(type: ExtraBuildingType): String = when (type) {
    ExtraBuildingType.GARAGE -> "Garage"
    ExtraBuildingType.CARPORT -> "Carport"
    ExtraBuildingType.SHED -> "Skjul"
    ExtraBuildingType.STOREHOUSE -> "Förråd"
    ExtraBuildingType.FRIGGEBOD -> "Friggebod"
    ExtraBuildingType.ATTEFALL -> "Attefallshus"
    ExtraBuildingType.OUTHOUSE -> "Uthus"
    ExtraBuildingType.GUESTHOUSE -> "Gästhus"
    ExtraBuildingType.GAZEBO -> "Lusthus"
    ExtraBuildingType.GREENHOUSE -> "Växthus"
    ExtraBuildingType.SAUNA -> "Bastu"
    ExtraBuildingType.BARN -> "Lada"
    ExtraBuildingType.BOATHOUSE -> "Båthus"
    ExtraBuildingType.OTHER -> "Övrigt"
}
