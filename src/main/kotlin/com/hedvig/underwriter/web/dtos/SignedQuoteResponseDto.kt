package com.hedvig.underwriter.web.dtos

import java.time.Instant
import java.util.UUID

data class SignedQuoteResponseDto(
    val id: UUID,
    val signedAt: Instant
)
