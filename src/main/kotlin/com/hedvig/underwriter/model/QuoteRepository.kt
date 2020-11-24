package com.hedvig.underwriter.model

import arrow.core.Either
import java.time.Instant
import java.util.UUID

interface QuoteRepository {
    fun find(quoteId: UUID): Quote?
    fun findQuotes(quoteIds: List<UUID>): List<Quote>
    fun insert(quote: Quote, timestamp: Instant = Instant.now())
    fun findByMemberId(memberId: String): List<Quote>
    fun findOneByMemberId(memberId: String): Quote?
    fun findLatestOneByMemberId(memberId: String): Quote?
    fun expireQuote(id: UUID): Quote?
    fun <A> modify(quoteId: UUID, modifier: (Quote?) -> Either<A, Quote>): Either<A, Quote>
    fun update(updatedQuote: Quote, timestamp: Instant = Instant.now()): Quote
    fun findByContractId(contractId: UUID): Quote?
}
