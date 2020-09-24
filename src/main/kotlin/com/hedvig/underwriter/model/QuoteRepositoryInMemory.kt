package com.hedvig.underwriter.model

import java.time.Instant
import java.util.UUID

class QuoteRepositoryInMemory : QuoteRepository {

    private val data = mutableMapOf<UUID, Quote>()

    override fun find(quoteId: UUID): Quote? {
        return data[quoteId]
    }

    override fun findQuotes(quoteIds: List<UUID>): List<Quote> {
        return data.filter { quoteIds.contains(it.key) }.values.toList()
    }

    override fun insert(quote: Quote, timestamp: Instant) {
        data[quote.id] = quote
    }

    override fun findByMemberId(memberId: String): List<Quote> {
        TODO("Not yet implemented")
    }

    override fun findOneByMemberId(memberId: String): Quote? {
        TODO("Not yet implemented")
    }

    override fun findLatestOneByMemberId(memberId: String): Quote? {
        TODO("Not yet implemented")
    }

    override fun expireQuote(id: UUID): Quote? {
        TODO("Not yet implemented")
    }

    override fun modify(quoteId: UUID, modifier: (Quote?) -> Quote?): Quote? {
        TODO("Not yet implemented")
    }

    override fun update(updatedQuote: Quote, timestamp: Instant): Quote {
        data[updatedQuote.id] = updatedQuote
        return updatedQuote
    }

    override fun findByContractId(contractId: UUID): Quote? {
        TODO("Not yet implemented")
    }
}
