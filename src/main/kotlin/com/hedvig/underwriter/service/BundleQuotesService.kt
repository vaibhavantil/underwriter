package com.hedvig.underwriter.service

import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.Quote
import java.util.UUID

interface BundleQuotesService {
    fun bundleQuotes(memberId: String?, ids: List<UUID>): BundledQuotes

    data class BundledQuotes(
        val quotes: List<Quote>,
        val cost: InsuranceCost
    )
}
