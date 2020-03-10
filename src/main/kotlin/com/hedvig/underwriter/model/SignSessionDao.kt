package com.hedvig.underwriter.model

import java.time.Instant
import java.util.UUID
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface SignSessionDao {

    @SqlUpdate(
        """
            INSERT INTO sign_sessions (
                id, created_at
            )
            VALUES (
                :id, :createdAt
            )
    """
    )
    fun insert(id: UUID, @Bind createdAt: Instant = Instant.now())

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
}
