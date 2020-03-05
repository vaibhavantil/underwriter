package com.hedvig.underwriter.graphql.type

data class UnderwritingLimitsHit(
    val limits: List<UnderwritingLimit>
) : CreateQuoteResult
