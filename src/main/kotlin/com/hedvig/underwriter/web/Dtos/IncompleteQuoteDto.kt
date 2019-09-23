package com.hedvig.underwriter.web.Dtos;

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.JsonTypeName
import com.hedvig.underwriter.model.*;

import java.time.Instant;

data class IncompleteQuoteDto (
    val quoteState: QuoteState,
    val createdAt: Instant,
    val productType: ProductType,
    val lineOfBusiness: LineOfBusiness?,
    val incompleteQuoteDataDto: IncompleteQuoteDataDto?,
    val quoteInitiatedFrom: QuoteInitiatedFrom?
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
        val address: String?,
        val numberOfRooms: Int?
)

