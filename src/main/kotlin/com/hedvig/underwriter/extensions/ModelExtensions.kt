package com.hedvig.underwriter.extensions

import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.graphql.type.CreateNorwegianHomeContentsInput
import com.hedvig.underwriter.graphql.type.CreateNorwegianTravelInput
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.CreateSwedishApartmentInput
import com.hedvig.underwriter.graphql.type.CreateSwedishHouseInput
import com.hedvig.underwriter.graphql.type.EditNorwegianHomeContentsInput
import com.hedvig.underwriter.graphql.type.EditNorwegianTravelInput
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.EditSwedishApartmentInput
import com.hedvig.underwriter.graphql.type.EditSwedishHouseInput
import com.hedvig.underwriter.graphql.type.ExtraBuildingInput
import com.hedvig.underwriter.graphql.type.ExtraBuildingType
import com.hedvig.underwriter.graphql.type.depricated.CreateApartmentInput
import com.hedvig.underwriter.graphql.type.depricated.CreateHouseInput
import com.hedvig.underwriter.graphql.type.depricated.EditApartmentInput
import com.hedvig.underwriter.graphql.type.depricated.EditHouseInput
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto
import com.hedvig.underwriter.util.toStockholmInstant
import java.util.UUID

fun CreateQuoteInput.toHouseOrApartmentIncompleteQuoteDto(
    quotingPartner: Partner? = null,
    memberId: String? = null,
    originatingProductId: UUID? = null
) = QuoteRequest(
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    currentInsurer = this.currentInsurer,
    birthDate = this.ssn.birthDateFromSwedishSsn(),
    ssn = this.ssn,
    productType = this.getProductType(),
    incompleteQuoteData = when {
        this.swedishApartment != null -> this.swedishApartment.toQuoteRequestData()
        this.swedishHouse != null -> this.swedishHouse.toQuoteRequestData()
        this.norweiganHomeContents != null -> this.norweiganHomeContents.toQuoteRequestData()
        this.norweiganTravel != null -> this.norweiganTravel.toQuoteRequestData()
        this.house != null -> this.house.toQuoteRequestData()
        else -> this.apartment!!.toQuoteRequestData()
    }
    ,
    quotingPartner = quotingPartner,
    memberId = memberId,
    originatingProductId = originatingProductId,
    startDate = this.startDate?.atStartOfDay()?.toStockholmInstant(),
    dataCollectionId = this.dataCollectionId
)

fun CreateApartmentInput.toQuoteRequestData() =
    QuoteRequestData.SwedishApartment(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        householdSize = this.householdSize,
        subType = this.type.toSubType(),
        city = null,
        floor = null
    )

fun CreateSwedishApartmentInput.toQuoteRequestData() =
    QuoteRequestData.SwedishApartment(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        householdSize = this.householdSize,
        subType = this.type.toSubType(),
        city = null,
        floor = null
    )

fun CreateHouseInput.toQuoteRequestData() =
    QuoteRequestData.SwedishHouse(
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

fun CreateSwedishHouseInput.toQuoteRequestData() =
    QuoteRequestData.SwedishHouse(
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

fun CreateNorwegianHomeContentsInput.toQuoteRequestData() =
    QuoteRequestData.NorwegianHomeContents(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        coinsured = this.coinsured,
        type = NorwegianHomeContentsType.valueOf(this.type.name),
        isStudent = this.isStudent,
        city = null
    )

fun CreateNorwegianTravelInput.toQuoteRequestData() =
    QuoteRequestData.NorwegianTravel(
        coinsured = this.coinsured
    )

fun EditQuoteInput.toHouseOrApartmentIncompleteQuoteDto(
    quotingPartner: Partner? = null,
    memberId: String? = null,
    originatingProductId: UUID? = null
) = QuoteRequest(
    firstName = this.firstName,
    lastName = this.lastName,
    email = this.email,
    currentInsurer = this.currentInsurer,
    birthDate = this.ssn?.birthDateFromSwedishSsn(),
    ssn = this.ssn,
    productType = this.getProductType(),
    incompleteQuoteData = when {
        this.swedishApartment != null -> this.swedishApartment.toQuoteRequestDataDto()
        this.swedishHouse != null -> this.swedishHouse.toQuoteRequestDataDto()
        this.norweiganHomeContents != null -> this.norweiganHomeContents.toQuoteRequestDataDto()
        this.norweiganTravel != null -> this.norweiganTravel.toQuoteRequestDataDto()
        this.apartment != null -> this.apartment.toQuoteRequestDataDto()
        this.house != null -> this.house.toQuoteRequestDataDto()
        else -> null
    },
    quotingPartner = quotingPartner,
    memberId = memberId,
    originatingProductId = originatingProductId,
    startDate = this.startDate?.atStartOfDay()?.toStockholmInstant(),
    dataCollectionId = this.dataCollectionId
)

fun EditSwedishApartmentInput.toQuoteRequestDataDto() =
    QuoteRequestData.SwedishApartment(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        householdSize = this.householdSize,
        subType = this.type?.toSubType(),
        city = null,
        floor = null
    )

fun EditSwedishHouseInput.toQuoteRequestDataDto() =
    QuoteRequestData.SwedishHouse(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        householdSize = this.householdSize,
        ancillaryArea = this.ancillarySpace,
        yearOfConstruction = this.yearOfConstruction,
        isSubleted = this.isSubleted,
        extraBuildings = this.extraBuildings?.toExtraBuilding(),
        numberOfBathrooms = this.numberOfBathrooms,
        city = null
    )

fun EditNorwegianHomeContentsInput.toQuoteRequestDataDto() =
    QuoteRequestData.NorwegianHomeContents(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        coinsured = this.coinsured,
        type = this.type?.let { NorwegianHomeContentsType.valueOf(it.name) },
        isStudent = this.isStudent,
        city = null
    )

fun EditNorwegianTravelInput.toQuoteRequestDataDto() =
    QuoteRequestData.NorwegianTravel(
        coinsured = coinsured
    )

fun EditApartmentInput.toQuoteRequestDataDto() =
    QuoteRequestData.SwedishApartment(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        householdSize = this.householdSize,
        subType = this.type?.toSubType(),
        city = null,
        floor = null
    )

fun EditHouseInput.toQuoteRequestDataDto() =
    QuoteRequestData.SwedishHouse(
        street = this.street,
        zipCode = this.zipCode,
        livingSpace = this.livingSpace,
        householdSize = this.householdSize,
        ancillaryArea = this.ancillarySpace,
        yearOfConstruction = this.yearOfConstruction,
        isSubleted = this.isSubleted,
        extraBuildings = this.extraBuildings?.toExtraBuilding(),
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

fun EditQuoteInput.getProductType(): ProductType? =
    this.apartment?.let {
        ProductType.APARTMENT
    } ?: this.house?.let {
        ProductType.HOUSE
    }

fun ApartmentType.toSubType(): ApartmentProductSubType = when (this) {
    ApartmentType.STUDENT_RENT -> ApartmentProductSubType.STUDENT_RENT
    ApartmentType.RENT -> ApartmentProductSubType.RENT
    ApartmentType.STUDENT_BRF -> ApartmentProductSubType.STUDENT_BRF
    ApartmentType.BRF -> ApartmentProductSubType.BRF
}
