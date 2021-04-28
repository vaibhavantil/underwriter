package com.hedvig.underwriter.service

import com.hedvig.productPricingObjects.enums.AgreementStatus
import com.hedvig.underwriter.model.AddressData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.birthDateMaybe
import com.hedvig.underwriter.model.ssnMaybe
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.util.logger
import org.springframework.stereotype.Service

@Service
class RequotingService(
    private val quoteRepository: QuoteRepository,
    private val productPricingService: ProductPricingService
) {

    fun blockDueToExistingAgreement(quote: Quote): Boolean {
        try {

            logger.debug("Check if existing agreement for quote. Initiated from ${quote.initiatedFrom}")

            // Do not block "internal" requests
            if (!quote.initiatedFrom.isAnyOf(
                    QuoteInitiatedFrom.ANDROID,
                    QuoteInitiatedFrom.IOS,
                    QuoteInitiatedFrom.RAPIO,
                    QuoteInitiatedFrom.WEBONBOARDING
                )
            ) {
                return false
            }

            return hasExistingAgreement(quote)
        } catch (e: Exception) {
            logger.warn("Failed to check if to block due to active agreement", e)
            return false
        }
    }

    private fun hasExistingAgreement(quote: Quote): Boolean {

        val data = quote.data

        // We require ssn or birthdate to be able to identify/fingerprint customer
        if (data !is PersonPolicyHolder<*> || (data.ssn == null && data.birthDate == null)) {
            return false
        }

        // We require address to be able to identify/fingerprint customer
        if (data !is AddressData || data.street == null || data.zipCode == null) {
            return false
        }

        // Get all existing quotes user's address
        var quotes = quoteRepository.findQuotesByAddress(data.street!!, data.zipCode!!, quote.data)

        if (quotes.isEmpty()) {
            return false
        }

        logger.debug("Found ${quotes.size} existing quote(s) for same address: {}", quotes.joinToString(separator = ", ") { it.id.toString() })

        // Filter out all signed quotes for user's ssn and birth date having an agreement
        quotes = quotes
            .filter { it.state == QuoteState.SIGNED }
            .filter { it.agreementId != null }
            .filter { it.birthDateMaybe == quote.birthDateMaybe }
            .filter { quote.ssnMaybe == null || it.ssnMaybe == null || it.ssnMaybe == quote.ssnMaybe }

        logger.debug("Found ${quotes.size} existing signed quote(s) for same birth date/ssn: {}", quotes.joinToString(separator = ", ") { it.id.toString() })

        for (existingQuote in quotes) {
            val agreement = productPricingService.getAgreement(existingQuote.agreementId!!)

            // Is agreement active?
            val active = agreement.status.isAnyOf(
                AgreementStatus.PENDING,
                AgreementStatus.ACTIVE,
                AgreementStatus.ACTIVE_IN_FUTURE
            )

            if (active) {
                logger.info("Quote matches an already signed quote: ${existingQuote.id} with an active agreement: ${agreement.id} - ${agreement.status}")
                return true
            }
        }

        return false
    }

    private fun QuoteInitiatedFrom.isAnyOf(vararg initiatedFrom: QuoteInitiatedFrom): Boolean =
        initiatedFrom.any { this == it }

    private fun AgreementStatus.isAnyOf(vararg statuses: AgreementStatus): Boolean =
        statuses.any { this == it }
}
