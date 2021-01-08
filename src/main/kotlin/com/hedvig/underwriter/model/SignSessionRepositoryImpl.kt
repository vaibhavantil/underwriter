package com.hedvig.underwriter.model

import com.hedvig.underwriter.service.model.SignMethod
import java.util.UUID
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.attach
import org.springframework.stereotype.Component

@Component
class SignSessionRepositoryImpl(private val jdbi: Jdbi) : SignSessionRepository {

    override fun insert(signMethod: SignMethod, quoteIds: List<UUID>): UUID {
        val signSessionId = UUID.randomUUID()
        jdbi.useTransaction<RuntimeException> { h ->
            val dao = h.attach<SignSessionDao>()
            dao.insert(signSessionId, signMethod)
            quoteIds.forEach { quoteId -> dao.insert(signSessionId, quoteId) }
        }
        return signSessionId
    }

    override fun find(sessionId: UUID): List<UUID> =
        jdbi.inTransaction<List<UUID>, RuntimeException> { h -> find(sessionId, h) }

    private fun find(sessionId: UUID, h: Handle) = h.attach<SignSessionDao>().findQuotes(sessionId)

    override fun findSignMethod(sessionId: UUID) =
        jdbi.inTransaction<SignMethod?, RuntimeException> { h ->
            h.attach<SignSessionDao>().findSignMethod(sessionId)
        }
}
