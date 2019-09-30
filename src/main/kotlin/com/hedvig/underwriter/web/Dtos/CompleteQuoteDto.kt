package com.hedvig.underwriter.web.Dtos;

import com.hedvig.underwriter.model.LineOfBusiness
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import java.time.Instant
import java.time.LocalDate
import javax.money.MonetaryAmount

data class CompleteQuoteDto (
        val quoteState: QuoteState,
        val quoteCreatedAt: Instant,
        val productType: ProductType,
        val lineOfBusiness: LineOfBusiness,
        val completeQuoteData: completeQuoteDataDto,
        val price: MonetaryAmount?,
        val quoteInitiatedFrom: QuoteInitiatedFrom,
        val birthDate: LocalDate,
        val livingSpace: Int,
        val houseHoldSize: Int,
        val isStudent: Boolean,
        val ssn: String
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
        val householdSize: Int
    )

    data class completeHomeQuoteDataDto(
        val address: String,
        val numberOfRooms: Int,
        val zipCode: String,
        val floor: Int
    )

