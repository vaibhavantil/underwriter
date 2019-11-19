package com.hedvig.underwriter.graphql.type

import com.hedvig.type.MonetaryAmountV2
import java.time.Instant

sealed class QuoteResult {

    data class Quote(
        val id: String,
        val firstName: String,
        val lastName: String,
        val currentInsurer: String?,
        val price: MonetaryAmountV2,
        val details: QuoteDetails,
        val expiresAt: Instant
    ) : QuoteResult()

    data class UnderwritingLimitsHit(
        val limits: List<UnderwritingLimit>
    ) : QuoteResult()

}
