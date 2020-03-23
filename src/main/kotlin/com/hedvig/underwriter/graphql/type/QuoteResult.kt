package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.depricated.CompleteQuoteDetails
import java.time.LocalDate
import java.util.UUID

sealed class QuoteResult {

    data class CompleteQuote(
        val id: UUID,
        val firstName: String,
        val lastName: String,
        val currentInsurer: CurrentInsurer?,
        val ssn: String,
        val birthDate: LocalDate,
        val price: MonetaryAmountV2,
        val insuranceCost: InsuranceCost,
        @Deprecated("use quoteDetails")
        val details: CompleteQuoteDetails,
        val quoteDetails: QuoteDetails,
        val startDate: LocalDate?,
        val expiresAt: LocalDate,
        val email: String?,
        val dataCollectionId: UUID?
    ) : QuoteResult(), CreateQuoteResult {
        val typeOfContract: TypeOfContract
            get() = when (quoteDetails) {
                is QuoteDetails.SwedishHouseQuoteDetails -> TypeOfContract.SE_HOUSE
                is QuoteDetails.SwedishApartmentQuoteDetails -> when (quoteDetails.type) {
                    ApartmentType.BRF -> TypeOfContract.SE_APARTMENT_BRF
                    ApartmentType.RENT -> TypeOfContract.SE_APARTMENT_RENT
                    ApartmentType.STUDENT_BRF -> TypeOfContract.SE_APARTMENT_STUDENT_BRF
                    ApartmentType.STUDENT_RENT -> TypeOfContract.SE_APARTMENT_STUDENT_RENT
                }
                is QuoteDetails.NorwegianHomeContentsDetails -> when (quoteDetails.type) {
                    NorwegianHomeContentsType.OWN -> {
                        when (quoteDetails.isYouth) {
                            true -> TypeOfContract.NO_HOME_CONTENT_YOUTH_OWN
                            false -> TypeOfContract.NO_HOME_CONTENT_OWN
                        }
                    }
                    NorwegianHomeContentsType.RENT -> {
                        when (quoteDetails.isYouth) {
                            true -> TypeOfContract.NO_HOME_CONTENT_YOUTH_RENT
                            false -> TypeOfContract.NO_HOME_CONTENT_RENT
                        }
                    }
                }
                is QuoteDetails.NorwegianTravelDetails -> {
                    when (quoteDetails.isYouth) {
                        true -> TypeOfContract.NO_TRAVEL_YOUTH
                        false -> TypeOfContract.NO_TRAVEL
                    }
                }
            }
    }

    @Deprecated("Incomplete is deprecated")
    data class IncompleteQuote(
        val id: UUID,
        val firstName: String?,
        val lastName: String?,
        val birthDate: LocalDate?,
        val currentInsurer: CurrentInsurer?,
        val details: IncompleteQuoteDetails?,
        val startDate: LocalDate?,
        val email: String?,
        val dataCollectionId: UUID?
    ) : QuoteResult()
}
