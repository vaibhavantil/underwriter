package com.hedvig.underwriter.graphql.type

import java.time.LocalDate
import java.util.UUID

sealed class QuoteResult {

    data class CompleteQuote(
        val id: UUID,
        val firstName: String,
        val lastName: String,
        val currentInsurer: CurrentInsurer?,
        val ssn: String,
        val insuranceCost: InsuranceCost,
        val details: CompleteQuoteDetails,
        val startDate: LocalDate?,
        val expiresAt: LocalDate
    ) : QuoteResult()

    data class IncompleteQuote(
        val id: UUID,
        val firstName: String?,
        val lastName: String?,
        val currentInsurer: CurrentInsurer?,
        val details: IncompleteQuoteDetails?,
        val startDate: LocalDate?
    ) : QuoteResult()

    data class UnderwritingLimitsHit(
        val limits: List<UnderwritingLimit>
    ) : QuoteResult()
}
