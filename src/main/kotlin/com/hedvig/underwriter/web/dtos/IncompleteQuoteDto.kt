package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import java.time.LocalDate

data class IncompleteQuoteDto(
    val apartmentProductSubType: ApartmentProductSubType?,
    val incompleteQuoteData: IncompleteQuoteDataDto?,
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val birthDate: LocalDate?,
    val ssn: String?,
    val quotingPartner: Partner?
)

data class IncompleteQuoteDataDto(
    val incompleteHouseQuoteData: IncompleteHouseQuoteDataDto?,
    val incompleteApartmentQuoteData: IncompleteApartmentQuoteDataDto?
)

data class IncompleteHouseQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val personalNumber: String?,
    val householdSize: Int?
)

data class IncompleteApartmentQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val floor: Int?
)
