package com.hedvig.underwriter.extensions

import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.graphql.type.CreateApartmentInput
import com.hedvig.underwriter.graphql.type.CreateHouseInput
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.ExtraBuildingInput
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.ProductType.*
import com.hedvig.underwriter.model.birthDateFromSsn
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto
import com.hedvig.underwriter.web.dtos.IncompleteApartmentQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteHouseQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import java.time.format.DateTimeFormatter
import java.util.*


private val ssnDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")

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
    incompleteApartmentQuoteData = this.apartment?.toIncompleteApartmentQuoteDataDto(),
    incompleteHouseQuoteData = this.house?.toIncompleteHouseQuoteDataDto(),
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
    personalNumber = null,
    city = null
)

fun List<ExtraBuildingInput>.toExtraBuilding(): List<ExtraBuildingRequestDto> = this.map { extraBuildingInput ->
    ExtraBuildingRequestDto(
        id = null,
        type = extraBuildingInput.type.toString(),
        area = extraBuildingInput.area,
        hasWaterConnected = extraBuildingInput.hasWaterConnected
    )
}


fun CreateQuoteInput.getProductType(): ProductType =
    this.apartment?.let {
        APARTMENT
    } ?: this.house?.let {
        HOUSE
    } ?: UNKNOWN

fun ApartmentType.toSubType(): ApartmentProductSubType = when (this) {
    ApartmentType.STUDENT_RENT -> ApartmentProductSubType.STUDENT_RENT
    ApartmentType.RENT -> ApartmentProductSubType.RENT
    ApartmentType.STUDENT_BRF -> ApartmentProductSubType.STUDENT_BRF
    ApartmentType.BRF -> ApartmentProductSubType.BRF
}
