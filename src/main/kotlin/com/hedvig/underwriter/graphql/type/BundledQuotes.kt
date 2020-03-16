package com.hedvig.underwriter.graphql.type

data class BundledQuotes(
    val quotes: List<QuoteResult.CompleteQuote>,
    val bundleCost: InsuranceCost
)
