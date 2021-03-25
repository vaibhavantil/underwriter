package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.depricated.CompleteQuoteDetails
import com.hedvig.libs.logging.masking.Masked
import java.time.LocalDate
import java.util.UUID

sealed class QuoteResult {

    data class CompleteQuote(
        val id: UUID,
        @Masked val firstName: String,
        @Masked val lastName: String,
        val currentInsurer: CurrentInsurer?,
        @Masked val ssn: String?,
        val birthDate: LocalDate,
        val price: MonetaryAmountV2,
        val insuranceCost: InsuranceCost,
        @Deprecated("use quoteDetails")
        val details: CompleteQuoteDetails,
        val quoteDetails: QuoteDetails,
        val startDate: LocalDate?,
        val expiresAt: LocalDate,
        val email: String?,
        @Masked val phoneNumber: String?,
        val dataCollectionId: UUID?
    ) : QuoteResult(), CreateQuoteResult {
        val typeOfContract: ContractAgreementType
            get() = quoteDetails.typeOfContract
    }

    @Deprecated("Incomplete is deprecated")
    data class IncompleteQuote(
        val id: UUID,
        @Masked val firstName: String?,
        @Masked val lastName: String?,
        val birthDate: LocalDate?,
        val currentInsurer: CurrentInsurer?,
        val details: IncompleteQuoteDetails?,
        val startDate: LocalDate?,
        @Masked val email: String?,
        val dataCollectionId: UUID?
    ) : QuoteResult()
}
