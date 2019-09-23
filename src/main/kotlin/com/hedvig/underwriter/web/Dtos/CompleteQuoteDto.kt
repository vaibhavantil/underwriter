package com.hedvig.underwriter.web.Dtos;

import com.hedvig.underwriter.model.LineOfBusiness
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import java.time.Instant

data class CompleteQuoteDto (
        val quoteState: QuoteState,
        val quoteCreatedAt: Instant,
        val productType: ProductType,
        val lineOfBusiness: LineOfBusiness,
        val completeQuoteData: completeQuoteDataDto,
        val price: Int,
        val quoteInitiatedFrom: QuoteInitiatedFrom
)

    data class completeQuoteDataDto(
        val completeHouseQuoteDataDto: completeHouseQuoteDataDto,
        val completeHomeQuoteDataDto: completeHomeQuoteDataDto
    )

    data class completeHouseQuoteDataDto(
        val street: String,
        val zipcode: String,
        val city: String,
        val livingSpace: Int,
        val personalNumber: String,
        val householdSize: Int
    )

    data class completeHomeQuoteDataDto(
        val address: String,
        val numberOfRooms: Int
    )

