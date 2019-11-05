package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingDto
import java.time.LocalDate
import java.util.UUID

data class IncompleteQuoteDto(
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val birthDate: LocalDate?,
    val ssn: String?,
    val quotingPartner: Partner?,
    val incompleteHouseQuoteData: IncompleteHouseQuoteDataDto?,
    val incompleteApartmentQuoteData: IncompleteApartmentQuoteDataDto?,
    val memberId: String? = null,
    val originatingProductId: UUID? = null
)

data class IncompleteHouseQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val personalNumber: String?,
    val householdSize: Int?,
    val ancillaryArea: Int?,
    val yearOfConstruction: Int?,
    val numberOfBathrooms: Int?,
    val extraBuildings: List<ExtraBuildingDto>?,
    val isSubleted: Boolean,
    val floor: Int = 0
)

data class IncompleteApartmentQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val floor: Int?,
    val subType: ApartmentProductSubType?
)
