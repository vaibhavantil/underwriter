package com.hedvig.underwriter.web.Dtos

import java.math.BigDecimal
import java.time.Instant

data class CompleteQuoteResponseDto (
        val id: String,
        val price: BigDecimal,
        val validTo: Instant?
)
