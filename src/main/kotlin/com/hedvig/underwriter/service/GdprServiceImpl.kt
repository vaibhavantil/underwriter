package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationService
import com.hedvig.underwriter.util.logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Service
class GdprServiceImpl(
    val quoteService: QuoteService,
    val notificationService: NotificationService,
    val quoteRepository: QuoteRepository
) : GdprService {

    @Value("\${features.gdpr.retention-days:-1}")
    private var days: Long = -1

    @Value("\${features.gdpr.dry-run:false}")
    private var dryRun: Boolean = false

    override fun clean() {
        try {
            run()
        } catch (e: Exception) {
            logger.error("Failed to execute cleaning job: $e", e)
        }
    }

    private fun run() {
        logger.info("Clean out quotes older than $days days")

        if (days <= 0) {
            logger.info("Cleaning disabled")
            return
        }

        val quotesToDelete = getQuotesToDelete(days)
        val membersToDelete = getMembersToDelete(quotesToDelete)

        logger.info("Found ${quotesToDelete.size} quote(s) to delete")
        logger.info("Found ${membersToDelete.size} member(s) to delete")

        deleteMembers(membersToDelete)
        deleteQuotes(quotesToDelete)

        logger.info("Successfully deleted ${quotesToDelete.size} quote(s) and ${membersToDelete.size} member(s)")
    }

    private fun getQuotesToDelete(days: Long): List<Quote> {
        val before = Instant.now().minus(days, ChronoUnit.DAYS)

        return quoteRepository.findOldQuotesToDelete(before)
    }

    private fun getMembersToDelete(quotes: List<Quote>): List<String> {

        // Set of quotes to clean
        val quoteIds = quotes
            .map { it.id }
            .toSet()

        // Set of members having quotes to clean
        val memberIds = quotes
            .map { it.memberId }
            .filterNotNull()
            .toSet()

        // If a member has no other quotes than in those to clean,
        // then the member can be deleted
        return memberIds.filter { hasNoOtherQuotes(it, quoteIds) }
    }

    private fun hasNoOtherQuotes(memberId: String, quotes: Set<UUID>): Boolean =
        quoteRepository.findByMemberId(memberId)
            .map { it.id }
            .all { quotes.contains(it) }

    private fun deleteMembers(memberIds: List<String>) {
        // Member Service
        // Endpoint not available yet

        // API GW
        // Endpoint not available yet

        // Notification Service
        for (id in memberIds) {
            logger.info("Deleting member $id in NotificationService ${if (dryRun) "(DRYRUN, SKIPPING)" else ""}")
            if (dryRun) {
                continue
            }
            notificationService.deleteMember(id)
        }
    }

    private fun deleteQuotes(quotes: List<Quote>) {
        // Lookup Service
        // Endpoint not available yet

        // Underwriter, we are deleting these last to get automatic retries
        // if any failure to delete members or quotes in other sources
        for (quote in quotes) {
            logger.info("Deleting quote ${quote.id} in Underwriter ${if (dryRun) "(DRYRUN, SKIPPING)" else ""}")
            if (dryRun) {
                continue
            }
            quoteService.deleteQuote(quote.id)
        }
    }
}
