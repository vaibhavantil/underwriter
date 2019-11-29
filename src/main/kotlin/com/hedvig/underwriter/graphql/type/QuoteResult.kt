package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.model.Quote
import java.time.Instant
import java.util.UUID

sealed class QuoteResult {

    data class CompleteQuote(
        val id: UUID,
        val firstName: String,
        val lastName: String,
        val currentInsurer: String?,
        val price: MonetaryAmountV2,
        val details: CompleteQuoteDetails,
        val expiresAt: Instant
    ) : QuoteResult()


    data class IncompleteQuote(
        val id: UUID,
        val firstName: String?,
        val lastName: String?,
        val currentInsurer: String?,
        val details: IncompleteQuoteDetails?
    ) : QuoteResult()

    data class UnderwritingLimitsHit(
        val limits: List<UnderwritingLimit>
    ) : QuoteResult()
}
