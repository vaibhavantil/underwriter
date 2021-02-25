package com.hedvig.underwriter.web.dtos

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class CompleteQuoteResponseDto(
    val id: UUID,
    val price: BigDecimal,
    val currency: String,
    val validTo: Instant
)
