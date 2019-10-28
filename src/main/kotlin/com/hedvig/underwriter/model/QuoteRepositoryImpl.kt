package com.hedvig.underwriter.model

import java.time.Instant
import java.util.UUID
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.attach
import org.springframework.stereotype.Component

@Component
class QuoteRepositoryImpl(private val jdbi: Jdbi) : QuoteRepository {

    override fun insert(
        quote: Quote,
        timestamp: Instant
    ) = jdbi.useTransaction<RuntimeException> { h ->
        val dao = h.attach<QuoteDao>()
        val quoteData: QuoteData = when (quote.data) {
            is ApartmentData -> dao.insert(quote.data)
            is HouseData -> dao.insert(quote.data)
        }
        dao.insertMasterQuote(quote.id, quote.initiatedFrom, timestamp)
        val databaseQuote = DatabaseQuoteRevision.from(quote.copy(data = quoteData))
        dao.insert(databaseQuote, timestamp)
    }

    override fun find(quoteId: UUID): Quote? =
        jdbi.inTransaction<Quote?, RuntimeException> { h -> find(quoteId, h) }

    fun find(quoteId: UUID, h: Handle): Quote? {
        val dao = h.attach<QuoteDao>()
        val databaseQuote = dao.find(quoteId) ?: return null
        val quoteData: QuoteData = when {
            databaseQuote.quoteApartmentDataId != null -> dao.findApartmentQuoteData(databaseQuote.quoteApartmentDataId)
            databaseQuote.quoteHouseDataId != null -> dao.findHouseQuoteData(databaseQuote.quoteHouseDataId)
            else -> throw IllegalStateException("Quote data must be apartment or house (but was neither) for quote $quoteId}")
        }!!
        return Quote(
            id = databaseQuote.masterQuoteId,
            validity = databaseQuote.validity,
            productType = databaseQuote.productType,
            state = databaseQuote.state,
            attributedTo = databaseQuote.attributedTo,
            currentInsurer = databaseQuote.currentInsurer,
            startDate = databaseQuote.startDate,
            price = databaseQuote.price,
            data = quoteData,
            memberId = databaseQuote.memberId,
            initiatedFrom = databaseQuote.initiatedFrom!!,
            createdAt = databaseQuote.createdAt!!
        )
    }

    override fun findByMemberId(memberId: String): Quote? =
        jdbi.inTransaction<Quote?, RuntimeException> { h -> findByMemberId(memberId, h) }

    fun findByMemberId(memberId: String, h: Handle): Quote? {
        val dao = h.attach<QuoteDao>()
        val databaseQuote = dao.findByMemberId(memberId) ?: return null
        val quoteData: QuoteData = when {
            databaseQuote.quoteApartmentDataId != null -> dao.findApartmentQuoteData(databaseQuote.quoteApartmentDataId)
            databaseQuote.quoteHouseDataId != null -> dao.findHouseQuoteData(databaseQuote.quoteHouseDataId)
            else -> throw IllegalStateException("Quote data must be apartment or house (but was neither) for quote ${databaseQuote.id}}")
        }!!
        return Quote(
            id = databaseQuote.masterQuoteId,
            validity = databaseQuote.validity,
            productType = databaseQuote.productType,
            state = databaseQuote.state,
            attributedTo = databaseQuote.attributedTo,
            currentInsurer = databaseQuote.currentInsurer,
            startDate = databaseQuote.startDate,
            price = databaseQuote.price,
            data = quoteData,
            memberId = databaseQuote.memberId,
            initiatedFrom = databaseQuote.initiatedFrom!!,
            createdAt = databaseQuote.createdAt!!
        )
    }

    override fun modify(quoteId: UUID, modifier: (Quote?) -> Quote?) =
        jdbi.inTransaction<Quote?, RuntimeException> { h ->
            val dao = h.attach<QuoteDao>()
            val modifiedQuote = modifier(find(quoteId, h))
            if (modifiedQuote != null) {
                update(modifiedQuote, Instant.now(), h)
            }
            return@inTransaction modifiedQuote
        }

    override fun update(updatedQuote: Quote, timestamp: Instant): Quote =
        jdbi.inTransaction<Quote, RuntimeException> { h ->
            update(updatedQuote, timestamp, h)
            find(updatedQuote.id, h)!!
        }

    private fun update(updatedQuote: Quote, timestamp: Instant, h: Handle) {
        val dao = h.attach<QuoteDao>()
        val quoteData: QuoteData = when (updatedQuote.data) {
            is ApartmentData -> dao.insert(updatedQuote.data)
            is HouseData -> dao.insert(updatedQuote.data)
        }
        dao.insert(DatabaseQuoteRevision.from(updatedQuote.copy(data = quoteData)), timestamp)
    }
}
