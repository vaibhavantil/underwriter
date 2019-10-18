package com.hedvig.underwriter.model

import java.util.UUID

interface QuoteRepository {
    fun find(quoteId: UUID): Quote?
    fun insert(quote: Quote)
    fun modify(quoteId: UUID, modifier: (Quote?) -> Quote?): Quote?
    fun update(updatedQuote: Quote): Quote
}
