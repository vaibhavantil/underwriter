package com.hedvig.underwriter.extensions

import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.graphql.type.CreateApartmentInput
import com.hedvig.underwriter.graphql.type.CreateHouseInput
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.EditApartmentInput
import com.hedvig.underwriter.graphql.type.EditHouseInput
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.ExtraBuildingInput
import com.hedvig.underwriter.graphql.type.ExtraBuildingType
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.birthDateFromSsn
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto
import com.hedvig.underwriter.util.toStockholmInstant
import com.hedvig.underwriter.web.dtos.IncompleteApartmentQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteHouseQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
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
    originatingProductId = originatingProductId,
    startDate = this.startDate?.atStartOfDay()?.toStockholmInstant()
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

fun EditQuoteInput.toIncompleteQuoteDto(
    quotingPartner: Partner? = null,
    memberId: String? = null,
    originatingProductId: UUID? = null
) = IncompleteQuoteDto(
    firstName = this.firstName,
    lastName = this.lastName,
    currentInsurer = this.currentInsurer,
    birthDate = this.ssn?.birthDateFromSsn(),
    ssn = this.ssn,
    productType = this.getProductType(),
    incompleteQuoteData = when {
        this.apartment != null -> this.apartment.toIncompleteApartmentQuoteDataDto()
        this.house != null -> this.house.toIncompleteHouseQuoteDataDto()
        else -> null
    },
    incompleteApartmentQuoteData = this.apartment?.toIncompleteApartmentQuoteDataDto(),
    incompleteHouseQuoteData = this.house?.toIncompleteHouseQuoteDataDto(),
    quotingPartner = quotingPartner,
    memberId = memberId,
    originatingProductId = originatingProductId
)

fun EditApartmentInput.toIncompleteApartmentQuoteDataDto() = IncompleteApartmentQuoteDataDto(
    street = this.street,
    zipCode = this.zipCode,
    livingSpace = this.livingSpace,
    householdSize = this.householdSize,
    subType = this.type?.toSubType(),
    city = null,
    floor = null
)

fun EditHouseInput.toIncompleteHouseQuoteDataDto() = IncompleteHouseQuoteDataDto(
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
    } ?: null

fun ApartmentType.toSubType(): ApartmentProductSubType = when (this) {
    ApartmentType.STUDENT_RENT -> ApartmentProductSubType.STUDENT_RENT
    ApartmentType.RENT -> ApartmentProductSubType.RENT
    ApartmentType.STUDENT_BRF -> ApartmentProductSubType.STUDENT_BRF
    ApartmentType.BRF -> ApartmentProductSubType.BRF
}
