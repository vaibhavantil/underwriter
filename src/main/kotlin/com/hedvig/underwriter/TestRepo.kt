package com.hedvig.underwriter

import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import org.springframework.stereotype.Repository
import java.util.UUID

interface TestRepo {
    @SqlUpdate(
        """
        INSERT INTO palmens_test (id) values (:id)
    """
    )
    fun insert(@Bind id: UUID)

    @SqlQuery(
        """
        SELECT * FROM palmens_test
    """
    )
    fun select(): List<UUID>
}
