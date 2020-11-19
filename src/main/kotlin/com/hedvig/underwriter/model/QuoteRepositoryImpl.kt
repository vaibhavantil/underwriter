package com.hedvig.underwriter.model

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.inTransactionUnchecked
import org.jdbi.v3.sqlobject.kotlin.attach
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID

@Component
class QuoteRepositoryImpl(private val jdbi: Jdbi) : QuoteRepository {

    override fun insert(
        quote: Quote,
        timestamp: Instant
    ) = jdbi.useTransaction<RuntimeException> { h ->
        val dao = h.attach<QuoteDao>()
        val quoteData: QuoteData = when (quote.data) {
            is SwedishApartmentData -> dao.insert(quote.data)
            is SwedishHouseData -> dao.insert(quote.data)
            is NorwegianHomeContentsData -> dao.insert(quote.data)
            is NorwegianTravelData -> dao.insert(quote.data)
            is DanishHomeContentsData -> dao.insert(quote.data)
            is DanishAccidentData -> dao.insert(quote.data)
            is DanishTravelData -> dao.insert(quote.data)
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
        return loadQuoteData(databaseQuote, dao)
    }

    override fun findQuotes(quoteIds: List<UUID>): List<Quote> =
        jdbi.inTransaction<List<Quote>, RuntimeException> { h -> findQuotes(quoteIds, h) }

    private fun findQuotes(quoteIds: List<UUID>, h: Handle): List<Quote> {
        val dao = h.attach<QuoteDao>()
        val databaseQuote = dao.find(quoteIds)
        return databaseQuote.mapNotNull { loadQuoteData(it, dao) }
    }

    override fun findByMemberId(memberId: String): List<Quote> =
        jdbi.inTransaction<List<Quote>, RuntimeException> { h -> findByMemberId(memberId, h) }

    fun findByMemberId(memberId: String, h: Handle): List<Quote> {
        val dao = h.attach<QuoteDao>()
        return dao.findByMemberId(memberId)
            .mapNotNull { databaseQuote -> loadQuoteData(databaseQuote, dao) }
    }

    override fun findOneByMemberId(memberId: String): Quote? =
        jdbi.inTransaction<Quote?, RuntimeException> { h -> findOneByMemberId(memberId, h) }

    fun findOneByMemberId(memberId: String, h: Handle): Quote? {
        val dao = h.attach<QuoteDao>()
        val databaseQuote = dao.findOneByMemberId(memberId) ?: return null
        return loadQuoteData(databaseQuote, dao)
    }

    override fun findLatestOneByMemberId(memberId: String): Quote? =
        jdbi.inTransaction<Quote?, RuntimeException> { h -> findLatestOneByMemberId(memberId, h) }

    fun findLatestOneByMemberId(memberId: String, h: Handle): Quote? {
        val dao = h.attach<QuoteDao>()
        val databaseQuotes = dao.findByMemberId(memberId)
        val latestQuote = databaseQuotes.maxBy { it.timestamp }
        return latestQuote?.let { quote -> loadQuoteData(quote, dao) }
    }

    override fun expireQuote(id: UUID): Quote? {
        return jdbi.inTransaction<Quote?, RuntimeException> { h -> expireQuote(id, h) }
    }

    fun expireQuote(id: UUID, h: Handle): Quote? {
        val dao = h.attach<QuoteDao>()
        val databaseQuotes = dao.find(id) ?: return null
        val expiredQuote = databaseQuotes.copy(validity = 0)
        update(expiredQuote, Instant.now(), h)
        return loadQuoteData(expiredQuote, dao)
    }

    fun loadQuoteData(databaseQuote: DatabaseQuoteRevision, dao: QuoteDao): Quote? {
        val quoteData: QuoteData = when {
            databaseQuote.quoteApartmentDataId != null -> dao.findApartmentQuoteData(databaseQuote.quoteApartmentDataId)
            databaseQuote.quoteHouseDataId != null -> dao.findHouseQuoteData(databaseQuote.quoteHouseDataId)
            databaseQuote.quoteNorwegianHomeContentsDataId != null -> dao.findNorwegianHomeContentsQuoteData(
                databaseQuote.quoteNorwegianHomeContentsDataId
            )
            databaseQuote.quoteNorwegianTravelDataId != null -> dao.findNorwegianTravelQuoteData(
                databaseQuote.quoteNorwegianTravelDataId
            )
            databaseQuote.quoteDanishHomeContentsDataId != null -> dao.findDanishHomeContentsQuoteData(
                databaseQuote.quoteDanishHomeContentsDataId
            )
            databaseQuote.quoteDanishAccidentDataId != null -> dao.findDanishAccidentQuoteData(
                databaseQuote.quoteDanishAccidentDataId
            )
            databaseQuote.quoteDanishTravelDataId != null -> dao.findDanishTravelQuoteData(
                databaseQuote.quoteDanishTravelDataId
            )
            else -> throw IllegalStateException("Quote must have details set (but was not). Quote ${databaseQuote.masterQuoteId} with quote revision ${databaseQuote.id}")
        }!!
        return Quote(
            id = databaseQuote.masterQuoteId,
            createdAt = databaseQuote.createdAt!!,
            price = databaseQuote.price,
            productType = databaseQuote.productType,
            state = databaseQuote.state,
            initiatedFrom = databaseQuote.initiatedFrom!!,
            attributedTo = databaseQuote.attributedTo,
            data = quoteData,
            currentInsurer = databaseQuote.currentInsurer,
            startDate = databaseQuote.startDate,
            validity = databaseQuote.validity,
            memberId = databaseQuote.memberId,
            breachedUnderwritingGuidelines = databaseQuote.breachedUnderwritingGuidelines,
            underwritingGuidelinesBypassedBy = databaseQuote.underwritingGuidelinesBypassedBy,
            originatingProductId = databaseQuote.originatingProductId,
            agreementId = databaseQuote.agreementId,
            dataCollectionId = databaseQuote.dataCollectionId,
            contractId = databaseQuote.contractId
        )
    }

    override fun modify(quoteId: UUID, modifier: (Quote?) -> Quote?) =
        jdbi.inTransaction<Quote?, RuntimeException> { h ->
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

    override fun findByContractId(contractId: UUID): Quote? {
        return jdbi.inTransactionUnchecked { h ->
            val dao = h.attach<QuoteDao>()
            val quoteRevision = dao.findByContractId(contractId)
            quoteRevision?.let { loadQuoteData(it, dao) }
        }
    }

    private fun update(updatedQuote: Quote, timestamp: Instant, h: Handle) {
        val dao = h.attach<QuoteDao>()
        val quoteData: QuoteData = when (updatedQuote.data) {
            is SwedishApartmentData -> dao.insert(updatedQuote.data)
            is SwedishHouseData -> dao.insert(updatedQuote.data)
            is NorwegianHomeContentsData -> dao.insert(updatedQuote.data)
            is NorwegianTravelData -> dao.insert(updatedQuote.data)
            is DanishHomeContentsData -> dao.insert(updatedQuote.data)
            is DanishAccidentData -> dao.insert(updatedQuote.data)
            is DanishTravelData -> dao.insert(updatedQuote.data)
        }
        dao.insert(DatabaseQuoteRevision.from(updatedQuote.copy(data = quoteData)), timestamp)
    }

    private fun update(updatedQuote: DatabaseQuoteRevision, timestamp: Instant, h: Handle) {
        val dao = h.attach<QuoteDao>()
        dao.insert(updatedQuote, timestamp)
    }
}
