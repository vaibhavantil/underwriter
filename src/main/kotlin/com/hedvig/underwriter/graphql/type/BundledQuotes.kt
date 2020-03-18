package com.hedvig.underwriter.graphql.type

data class BundledQuotes(
    val quotes: List<BundledQuote>,
    val bundleCost: InsuranceCost
)
