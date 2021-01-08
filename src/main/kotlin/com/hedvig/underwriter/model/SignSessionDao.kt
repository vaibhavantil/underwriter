package com.hedvig.underwriter.model

import com.hedvig.underwriter.service.model.SignMethod
import java.time.Instant
import java.util.UUID
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface SignSessionDao {

    @SqlUpdate(
        """
            INSERT INTO sign_sessions (
                id, signMethod, created_at
            )
            VALUES (
                :id, :signMethod, :createdAt
            )
    """
    )
    fun insert(id: UUID, signMethod: SignMethod, @Bind createdAt: Instant = Instant.now())

    @SqlUpdate(
        """
            INSERT INTO sign_session_master_quote (
                sign_session_id, master_quote_id
            ) 
            VALUES (
                :signSessionId, :masterQuoteId
            )
    """
    )
    fun insert(signSessionId: UUID, masterQuoteId: UUID)

    @SqlQuery(
        """
            SELECT master_quote_id 
            FROM sign_session_master_quote
            WHERE sign_session_id = :sessionId
        """
    )
    fun findQuotes(sessionId: UUID): List<UUID>

    @SqlQuery(
        """
            SELECT signMethod 
            FROM sign_sessions
            WHERE id = :sessionId
        """
    )
    fun findSignMethod(sessionId: UUID): SignMethod?
}
