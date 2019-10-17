package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import java.time.LocalDate

data class IncompleteQuoteDto(
    val productType: ProductType,
    val apartmentProductSubType: ApartmentProductSubType?,
    val incompleteQuoteData: IncompleteQuoteDataDto?,
    val quoteInitiatedFrom: QuoteInitiatedFrom?,
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val birthDate: LocalDate?,
    val ssn: String?
)

data class IncompleteQuoteDataDto(
    val incompleteHouseQuoteData: IncompleteHouseQuoteDataDto?,
    val incompleteApartmentQuoteData: IncompleteHomeQuoteDataDto?
)

data class IncompleteHouseQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val personalNumber: String?,
    val householdSize: Int?
)

data class IncompleteHomeQuoteDataDto(
    var street: String?,
    var zipCode: String?,
    var city: String?,
    var livingSpace: Int?,
    var houseHoldSize: Int?,
    var floor: Int?
)
