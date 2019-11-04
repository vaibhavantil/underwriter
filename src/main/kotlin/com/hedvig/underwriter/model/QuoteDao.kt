package com.hedvig.underwriter.model

import java.time.Instant
import java.util.UUID
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface QuoteDao {
    @SqlUpdate(
        """
            INSERT INTO quote_revisions (
                master_quote_id,
                timestamp,
                validity,
                product_type,
                state,
                attributed_to,
                current_insurer,
                start_date,
                price,
                quote_apartment_data_id,
                quote_house_data_id,
                member_id
            )
            VALUES (
                :masterQuoteId,
                :timestamp,
                :validity,
                :productType,
                :state,
                :attributedTo,
                :currentInsurer,
                :startDate,
                :price,
                :quoteApartmentDataId,
                :quoteHouseDataId,
                :memberId
            )
            RETURNING *
    """
    )
    @GetGeneratedKeys("id")
    fun insert(@BindBean quote: DatabaseQuoteRevision, @Bind timestamp: Instant): DatabaseQuoteRevision

    @SqlQuery(
        """
            SELECT
            DISTINCT ON (qr.master_quote_id)

            qr.*,
            mq.created_at,
            mq.initiated_from

            FROM quote_revisions qr
            INNER JOIN master_quotes mq
                ON mq.id = qr.master_quote_id
            WHERE qr.master_quote_id = :quoteId
            ORDER BY qr.master_quote_id ASC, qr.id DESC
        """
    )
    fun find(@Bind quoteId: UUID): DatabaseQuoteRevision?

    @SqlUpdate(
        """
            INSERT INTO quote_revision_apartment_data
            (id, ssn, first_name, last_name, street, city, zip_code, household_size, living_space, sub_type)
            VALUES
            (:id, :ssn, :firstName, :lastName, :street, :city, :zipCode, :householdSize, :livingSpace, :subType)
            RETURNING *
        """
    )
    @GetGeneratedKeys("internal_id")
    fun insert(@BindBean quoteData: ApartmentData): ApartmentData

    @SqlQuery("""
        SELECT * FROM quote_revision_apartment_data WHERE internal_id = :id""")
    fun findApartmentQuoteData(@Bind id: Int): ApartmentData?

    @SqlQuery(
        """
            SELECT
            DISTINCT ON (qr.master_quote_id)
            
            qr.*, 
            mq.created_at, 
            mq.initiated_from 
            
            FROM master_quotes mq
            JOIN quote_revisions qr
            ON qr.master_quote_id = mq.id 
            
            WHERE qr.member_id = :memberId
            ORDER BY qr.master_quote_id ASC, qr.id DESC
        """
    )
    fun findByMemberId(@Bind memberId: String): DatabaseQuoteRevision?

    @SqlUpdate(
        """
            INSERT INTO quote_revision_house_data
            (id, ssn, first_name, last_name, street, city, zip_code, household_size, living_space)
            VALUES
            (:id, :ssn, :firstName, :lastName, :street, :city, :zipCode, :householdSize, :livingSpace)
            RETURNING *
    """
    )
    @GetGeneratedKeys("internal_id")
    fun insert(@BindBean data: HouseData): HouseData

    @SqlQuery(
        """
        SELECT * FROM quote_revision_house_data WHERE internal_id = :id
    """
    )
    fun findHouseQuoteData(@Bind id: Int): HouseData?

    @SqlUpdate(
        """
        INSERT INTO master_quotes (id, initiated_from, created_at) VALUES (:quoteId, :initiatedFrom, :createdAt)
    """
    )
    fun insertMasterQuote(
        @Bind quoteId: UUID,
        @Bind initiatedFrom: QuoteInitiatedFrom,
        @Bind createdAt: Instant = Instant.now()
    )
}
