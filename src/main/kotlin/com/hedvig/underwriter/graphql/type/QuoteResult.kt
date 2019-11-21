package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import java.time.Instant
import java.util.*

sealed class QuoteResult {

    data class Quote(
        val id: UUID,
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
