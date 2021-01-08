package com.hedvig.underwriter.model

import com.hedvig.underwriter.service.model.SignMethod
import java.util.UUID

interface SignSessionRepository {
    fun insert(signMethod: SignMethod, quoteIds: List<UUID>): UUID
    fun find(sessionId: UUID): List<UUID>
    fun findSignMethod(sessionId: UUID): SignMethod?
}
