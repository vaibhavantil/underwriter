package com.hedvig.underwriter.extensions

import com.hedvig.service.LocalizationService
import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.graphql.type.CompleteQuoteDetails
import com.hedvig.underwriter.graphql.type.CreateApartmentInput
import com.hedvig.underwriter.graphql.type.CreateHouseInput
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.ExtraBuilding
import com.hedvig.underwriter.graphql.type.ExtraBuildingInput
import com.hedvig.underwriter.graphql.type.ExtraBuildingType
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.birthDateFromSsn
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto
import com.hedvig.underwriter.web.dtos.IncompleteApartmentQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteHouseQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import java.lang.IllegalStateException
import java.util.Locale
import java.util.UUID

fun CreateQuoteInput.toIncompleteQuoteDto(
    quotingPartner: Partner? = null,
    memberId: String? = null,
    originatingProductId: UUID? = null
) = IncompleteQuoteDto(
    firstName = this.firstName,
    lastName = this.lastName,
    currentInsurer = this.currentInsurer,
    birthDate = this.ssn.birthDateFromSsn(),
    ssn = this.ssn,
    productType = this.getProductType(),
    incompleteQuoteData = if (this.house != null) this.house.toIncompleteHouseQuoteDataDto() else this.apartment!!.toIncompleteApartmentQuoteDataDto(),
    incompleteHouseQuoteData = this.house?.toIncompleteHouseQuoteDataDto(),
    incompleteApartmentQuoteData = this.apartment?.toIncompleteApartmentQuoteDataDto(),
    quotingPartner = quotingPartner,
    memberId = memberId,
    originatingProductId = originatingProductId
)

fun CreateApartmentInput.toIncompleteApartmentQuoteDataDto() = IncompleteApartmentQuoteDataDto(
    street = this.street,
    zipCode = this.zipCode,
    livingSpace = this.livingSpace,
    householdSize = this.householdSize,
    subType = this.type.toSubType(),
    city = null,
    floor = null
)

fun CreateHouseInput.toIncompleteHouseQuoteDataDto() = IncompleteHouseQuoteDataDto(
    street = this.street,
    zipCode = this.zipCode,
    livingSpace = this.livingSpace,
    householdSize = this.householdSize,
    ancillaryArea = this.ancillarySpace,
    yearOfConstruction = this.yearOfConstruction,
    isSubleted = this.isSubleted,
    extraBuildings = this.extraBuildings.toExtraBuilding(),
    numberOfBathrooms = this.numberOfBathrooms,
    city = null
)

fun List<ExtraBuildingInput>.toExtraBuilding(): List<ExtraBuildingRequestDto> = this.map { extraBuildingInput ->
    ExtraBuildingRequestDto(
        id = null,
        type = extraBuildingInput.type.toExtraBuildingType(),
        area = extraBuildingInput.area,
        hasWaterConnected = extraBuildingInput.hasWaterConnected
    )
}

private fun ExtraBuildingType.toExtraBuildingType() = when (this) {
    ExtraBuildingType.GARAGE -> com.hedvig.underwriter.model.ExtraBuildingType.GARAGE
    ExtraBuildingType.CARPORT -> com.hedvig.underwriter.model.ExtraBuildingType.CARPORT
    ExtraBuildingType.SHED -> com.hedvig.underwriter.model.ExtraBuildingType.SHED
    ExtraBuildingType.STOREHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.STOREHOUSE
    ExtraBuildingType.FRIGGEBOD -> com.hedvig.underwriter.model.ExtraBuildingType.FRIGGEBOD
    ExtraBuildingType.ATTEFALL -> com.hedvig.underwriter.model.ExtraBuildingType.ATTEFALL
    ExtraBuildingType.OUTHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.OUTHOUSE
    ExtraBuildingType.GUESTHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.GUESTHOUSE
    ExtraBuildingType.GAZEBO -> com.hedvig.underwriter.model.ExtraBuildingType.GAZEBO
    ExtraBuildingType.GREENHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.GREENHOUSE
    ExtraBuildingType.SAUNA -> com.hedvig.underwriter.model.ExtraBuildingType.SAUNA
    ExtraBuildingType.BARN -> com.hedvig.underwriter.model.ExtraBuildingType.BARN
    ExtraBuildingType.BOATHOUSE -> com.hedvig.underwriter.model.ExtraBuildingType.BOATHOUSE
    ExtraBuildingType.OTHER -> com.hedvig.underwriter.model.ExtraBuildingType.OTHER
}

fun CreateQuoteInput.getProductType(): ProductType =
    this.apartment?.let {
        ProductType.APARTMENT
    } ?: this.house?.let {
        ProductType.HOUSE
    } ?: ProductType.UNKNOWN

fun ApartmentType.toSubType(): ApartmentProductSubType = when (this) {
    ApartmentType.STUDENT_RENT -> ApartmentProductSubType.STUDENT_RENT
    ApartmentType.RENT -> ApartmentProductSubType.RENT
    ApartmentType.STUDENT_BRF -> ApartmentProductSubType.STUDENT_BRF
    ApartmentType.BRF -> ApartmentProductSubType.BRF
}

fun CreateQuoteInput.createCompleteQuoteResult(
    localizationService: LocalizationService,
    locale: Locale
): CompleteQuoteDetails =
    this.apartment?.let { apartment ->
        CompleteQuoteDetails.CompleteApartmentQuoteDetails(
            street = apartment.street,
            zipCode = apartment.zipCode,
            householdSize = apartment.householdSize,
            livingSpace = apartment.livingSpace,
            type = apartment.type
        )
    } ?: this.house?.let { house ->
        CompleteQuoteDetails.CompleteHouseQuoteDetails(
            street = house.street,
            zipCode = house.zipCode,
            householdSize = house.householdSize,
            livingSpace = house.livingSpace,
            ancillarySpace = house.ancillarySpace,
            extraBuildings = house.extraBuildings.map { extraBuildingInput ->
                when (extraBuildingInput.type) {
                    ExtraBuildingType.GARAGE -> ExtraBuilding.ExtraBuildingGarage(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.CARPORT -> ExtraBuilding.ExtraBuildingCarport(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.SHED -> ExtraBuilding.ExtraBuildingShed(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.STOREHOUSE -> ExtraBuilding.ExtraBuildingStorehouse(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.FRIGGEBOD -> ExtraBuilding.ExtraBuildingFriggebod(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.ATTEFALL -> ExtraBuilding.ExtraBuildingAttefall(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.OUTHOUSE -> ExtraBuilding.ExtraBuildingOuthouse(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.GUESTHOUSE -> ExtraBuilding.ExtraBuildingGuesthouse(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.GAZEBO -> ExtraBuilding.ExtraBuildingGazebo(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.GREENHOUSE -> ExtraBuilding.ExtraBuildingGreenhouse(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.SAUNA -> ExtraBuilding.ExtraBuildingSauna(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.BARN -> ExtraBuilding.ExtraBuildingBarn(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.BOATHOUSE -> ExtraBuilding.ExtraBuildingBoathouse(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                    ExtraBuildingType.OTHER -> ExtraBuilding.ExtraBuildingOther(
                        area = extraBuildingInput.area,
                        hasWaterConnected = extraBuildingInput.hasWaterConnected,
                        displayName = extraBuildingInput.type.getDisplayName(localizationService, locale)
                    )
                }
            }
        )
    } ?: throw IllegalStateException("Trying to create QuoteDetails without `apartment` or `house` data")

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
