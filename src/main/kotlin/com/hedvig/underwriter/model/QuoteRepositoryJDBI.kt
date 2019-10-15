package com.hedvig.underwriter.model

import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.onDemand
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.*

@Profile("runtime")
@Component
class QuoteRepositoryJDBI(val jdbi: Jdbi) : QuoteRepository {
    override fun insert(quote: Quote) {
        jdbi.onDemand<QuoteJDBIDao>().insertQuote(quote)
    }

    override fun load(quoteId: UUID): Quote? {
        return jdbi.onDemand<QuoteJDBIDao>().loadQuote(quoteId)
    }

    override fun save(quote: Quote) {
        return jdbi.onDemand<QuoteJDBIDao>().update(quote)
    }
}