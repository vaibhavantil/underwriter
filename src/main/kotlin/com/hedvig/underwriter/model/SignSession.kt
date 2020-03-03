package com.hedvig.underwriter.model

import java.util.UUID

data class SignSession(
    val sessionId: UUID,
    val quotesToBeSigned: List<Quote>
)
