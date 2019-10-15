package com.hedvig.underwriter.model

import java.util.*

interface QuoteRepository {
    fun load(quoteId: UUID): Quote?
    fun save(quote: Quote)
    fun insert(quote: Quote)

}
