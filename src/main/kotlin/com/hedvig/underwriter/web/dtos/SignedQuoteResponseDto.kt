package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.Market
import java.time.Instant
import java.util.UUID

data class SignedQuoteResponseDto(
    val id: UUID, // contractId
    val memberId: String,
    val signedAt: Instant,
    val market: Market
)
