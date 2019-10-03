package com.hedvig.underwriter.web.Dtos;

import com.hedvig.underwriter.model.*;

import java.time.Instant;
import java.time.LocalDate

data class IncompleteQuoteDto (
        val quoteState: QuoteState,
        val createdAt: Instant,
        val productType: ProductType,
        val lineOfBusiness: LineOfBusiness?,
        val incompleteQuoteDataDto: IncompleteQuoteDataDto?,
        val quoteInitiatedFrom: QuoteInitiatedFrom?,
        var firstName: String?,
        var lastName: String?,
        var currentInsurer: String?,
        val birthDate: LocalDate?,
        val livingSpace: Int?,
        val houseHoldSize: Int?,
        val isStudent: Boolean?,
        val ssn: String?
)

data class IncompleteQuoteDataDto(
        val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto?,
        val incompleteHomeQuoteDataDto: IncompleteHomeQuoteDataDto?
)

data class IncompleteHouseQuoteDataDto(
        val street: String?,
        val zipcode: String?,
        val city: String?,
        val livingSpace: Int?,
        val personalNumber: String?,
        val householdSize: Int?
)

data class IncompleteHomeQuoteDataDto(
        var street: String?,
        var city: String?,
        var zipCode: String?,
        var numberOfRooms: Int?
)

