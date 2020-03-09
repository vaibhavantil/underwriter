package com.hedvig.underwriter.model

import org.jdbi.v3.sqlobject.statement.SqlBatch
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.UUID

interface SignSessionDao {

    @SqlUpdate(
        """
            INSERT INTO sign_sessions (
                id
            )
            VALUES (
                :id
            )
    """
    )
    fun insert(id: UUID)

    @SqlUpdate(
        """
            INSERT INTO sign_session_quote_revision (
                sign_session_id, master_quote_id
            ) 
            VALUES (
                :signSessionId, :masterQuoteId
            )
    """
    )
    fun insert(signSessionId: UUID, masterQuoteId: UUID)
}
