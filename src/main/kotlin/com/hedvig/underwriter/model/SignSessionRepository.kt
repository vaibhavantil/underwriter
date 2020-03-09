package com.hedvig.underwriter.model

import java.util.UUID

interface SignSessionRepository {
    fun find(signSessionId: UUID): SignSession
    fun insert(quoteIds: List<UUID>): UUID
}
