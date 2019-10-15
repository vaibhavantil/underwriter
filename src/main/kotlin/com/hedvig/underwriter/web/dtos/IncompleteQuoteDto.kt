package com.hedvig.underwriter.web.dtos;

import com.hedvig.underwriter.model.*;

import java.time.Instant;
import java.time.LocalDate

data class IncompleteQuoteDto (
    val quoteState: QuoteState,
    val createdAt: Instant,
    val productType: ProductType,
    val homeProductSubType: HomeProductSubType?,
    val incompleteQuoteDataDto: IncompleteQuoteDataDto?,
    val quoteInitiatedFrom: QuoteInitiatedFrom?,
    var firstName: String?,
    var lastName: String?,
    var currentInsurer: String?,
    val birthDate: LocalDate?,
    val isStudent: Boolean?,
    val ssn: String?
)

data class IncompleteQuoteDataDto(
        val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto?,
        val incompleteHomeQuoteDataDto: IncompleteHomeQuoteDataDto?
)

data class IncompleteHouseQuoteDataDto(
        val street: String?,
        val zipCode: String?,
        val city: String?,
        val livingSpace: Int?,
        val houseHoldSize: Int?,
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

