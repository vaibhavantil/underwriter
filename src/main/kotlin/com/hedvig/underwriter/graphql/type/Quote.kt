package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import java.time.Instant
import java.util.UUID

sealed class Quote {

    data class CompleteQuote(
        val id: UUID,
        val currentInsurer: String?,
        val price: MonetaryAmountV2,
//        val details: CompleteQuoteDetails, TODO Let's add this later
        val expiresAt: Instant
    ) : Quote()

    data class IncompleteQuote(
        val id: UUID,
        val currentInsurer: String?
//        val details: IncompleteQuoteDetails TODO Let's add this later
    ) : Quote()
}
