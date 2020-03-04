package com.hedvig.underwriter.model

import org.springframework.stereotype.Component
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.sqlobject.kotlin.attach
import java.util.UUID

@Component
class SignSessionRepositoryImpl(private val jdbi: Jdbi) : SignSessionRepository {

    override fun insert(quoteIds: List<UUID>): UUID {
        val signSessionId = UUID.randomUUID()
        jdbi.useTransaction<RuntimeException> { h ->
            val dao = h.attach<SignSessionDao>()
            dao.insert(signSessionId)
            dao.insert(signSessionId, quoteIds)
        }
        return signSessionId
    }

    override fun find(signSessionId: UUID): SignSession {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
