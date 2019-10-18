package com.hedvig.underwriter.model

import java.util.UUID
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.attach
import org.springframework.stereotype.Component

@Component
class QuoteRepositoryImpl(private val jdbi: Jdbi) : QuoteRepository {
    override fun insert(quote: Quote) = jdbi.useTransaction<RuntimeException> { h ->
        val dao = h.attach<QuoteDao>()
        when (quote.data) {
            is ApartmentData -> dao.insert(quote.data)
            is HouseData -> dao.insert(quote.data)
        }
        val databaseQuote = DatabaseQuote.from(quote)
        dao.insert(databaseQuote)
    }

    override fun find(quoteId: UUID): Quote? =
        jdbi.inTransaction<Quote?, RuntimeException> { h -> find(quoteId, h) }

    fun find(quoteId: UUID, h: Handle): Quote? {
        val dao = h.attach<QuoteDao>()
        val databaseQuote = dao.find(quoteId) ?: return null
        val quoteData: QuoteData? = when {
            databaseQuote.quoteApartmentDataId != null -> dao.findApartmentQuoteData(databaseQuote.quoteApartmentDataId)
            databaseQuote.quoteHouseDataId != null -> dao.findHouseQuoteData(databaseQuote.quoteHouseDataId)
            else -> throw IllegalStateException("Quote data must be apartment or house (but was neither) for quote $quoteId}")
        }
        return Quote(
            id = databaseQuote.id,
            data = quoteData!!,
            createdAt = databaseQuote.createdAt,
            price = databaseQuote.price,
            currentInsurer = databaseQuote.currentInsurer,
            initiatedFrom = databaseQuote.initiatedFrom,
            attributedTo = databaseQuote.attributedTo,
            productType = databaseQuote.productType,
            startDate = databaseQuote.startDate,
            quotedAt = databaseQuote.quotedAt,
            signedAt = databaseQuote.signedAt
        )
    }

    override fun modify(quoteId: UUID, modifier: (Quote?) -> Quote?) =
        jdbi.inTransaction<Quote?, RuntimeException> { h ->
            val dao = h.attach<QuoteDao>()
            val modifiedQuote = modifier(find(quoteId, h))
            if (modifiedQuote != null) {
                update(modifiedQuote, h)
            }
            return@inTransaction modifiedQuote
        }

    override fun update(updatedQuote: Quote) = jdbi.useTransaction<RuntimeException> { h ->
        update(updatedQuote, h)
    }

    private fun update(updatedQuote: Quote, h: Handle) {
        val dao = h.attach<QuoteDao>()
        when (updatedQuote.data) {
            is ApartmentData -> dao.update(updatedQuote.data)
            is HouseData -> dao.update(updatedQuote.data)
        }
        dao.update(DatabaseQuote.from(updatedQuote))
    }
}
