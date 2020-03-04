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
            RETURNING *
    """
    )
    fun insert(id: UUID): UUID

    @SqlBatch(
        """
            INSERT INTO sign_session_quote_revision (
                sign_session_id, name
            ) 
            VALUES (
                id, master_quote_id
            )
    """
    )
    fun insert(id: UUID, quoteIds: List<UUID>)
}
