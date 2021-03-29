package com.hedvig.underwriter.model

import java.time.Instant
import java.util.UUID
import kotlin.reflect.KClass

interface QuoteRepository {
    fun find(quoteId: UUID): Quote?
    fun findQuotes(quoteIds: List<UUID>): List<Quote>
    fun insert(quote: Quote, timestamp: Instant = Instant.now())
    fun findByMemberId(memberId: String): List<Quote>
    fun findOneByMemberId(memberId: String): Quote?
    fun findLatestOneByMemberId(memberId: String): Quote?
    fun expireQuote(id: UUID): Quote?
    fun update(updatedQuote: Quote, timestamp: Instant = Instant.now()): Quote
    fun findByContractId(contractId: UUID): Quote?
    fun findQuotesByAddress(street: String, zipCode: String, type: KClass<*>): List<Quote>
}
