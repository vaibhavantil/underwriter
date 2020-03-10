package com.hedvig.underwriter.model

import java.util.UUID

interface SignSessionRepository {
    fun insert(quoteIds: List<UUID>): UUID
    fun find(sessionId: UUID): List<UUID>
}
