package com.hedvig.underwriter.web.dtos

import java.time.Instant
import java.util.UUID

data class SignedQuotesResponseDto(
    val memberId: String,
    val market: String,
    val contracts: List<Contract>
) {
    companion object {
        fun from(responses: List<SignedQuoteResponseDto>) = SignedQuotesResponseDto(
            responses[0].memberId,
            responses[0].market.name,
            responses.map { Contract(it.id, it.signedAt) }.toList()
        )
    }

    data class Contract(
        val id: UUID,
        val signedAt: Instant
    )
}
