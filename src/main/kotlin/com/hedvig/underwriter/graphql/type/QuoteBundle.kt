package com.hedvig.underwriter.graphql.type

data class QuoteBundle(
    val quotes: List<BundledQuote>,
    val bundleCost: InsuranceCost
)
