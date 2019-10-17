package com.hedvig.underwriter.model

import java.util.UUID
import org.jdbi.v3.sqlobject.customizer.Bind
import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlQuery
import org.jdbi.v3.sqlobject.statement.SqlUpdate

interface QuoteDao {
    @SqlUpdate(
        """
            INSERT INTO quotes 
            (id, created_at, quoted_at, signed_at, validity, product_type, initiated_from, current_insurer, start_date, price, quote_apartment_data_id, quote_house_data_id)
            VALUES
            (:id, :createdAt, :quotedAt, :signedAt, :validity, :productType, :initiatedFrom, :currentInsurer, :startDate, :price, :quoteApartmentDataId, :quoteHouseDataId)
    """
    )
    fun insert(@BindBean quote: DatabaseQuote)

    @SqlUpdate(
        """
            INSERT INTO quote_apartment_data
            (id, ssn, first_name, last_name, street, city, zip_code, household_size, living_space, sub_type)
            VALUES
            (:id, :ssn, :firstName, :lastName, :street, :city, :zipCode, :householdSize, :livingSpace, :subType)
        """
    )
    fun insert(@BindBean quoteData: ApartmentData)

    @SqlQuery("""SELECT * FROM "quotes" WHERE id = :quoteId;""")
    fun find(@Bind quoteId: UUID): DatabaseQuote?

    @SqlQuery("""SELECT * FROM quote_apartment_data WHERE id = :id""")
    fun findApartmentQuoteData(@Bind id: UUID): ApartmentData?

    @SqlUpdate("""
            INSERT INTO quote_house_data
            (id, ssn, first_name, last_name, street, city, zip_code, household_size, living_space)
            VALUES
            (:id, :ssn, :firstName, :lastName, :street, :city, :zipCode, :householdSize, :livingSpace)
    """)
    fun insert(@BindBean data: HouseData)

    @SqlQuery(
        """
        SELECT * FROM quote_house_data WHERE id = :id
    """
    )
    fun findHouseQuoteData(@Bind id: UUID): HouseData?

    @SqlUpdate("""
        UPDATE quotes
            SET
                quoted_at = :quotedAt,
                signed_at = :signedAt,
                created_at = :createdAt,
                product_type = :productType,
                initiated_from = :initiatedFrom,
                current_insurer = :currentInsurer,
                start_date = :startDate,
                price = :price,
                quote_apartment_data_id = :quoteApartmentDataId,
                quote_house_data_id = :quoteHouseDataId
            WHERE
                id = :id
    """)
    fun update(@BindBean quote: DatabaseQuote)

    @SqlUpdate("""
        UPDATE quote_apartment_data 
            SET
                ssn = :ssn,
                first_name = :firstName,
                last_name = :lastName,
                street = :street,
                city = :city,
                zip_code = :zipCode,
                household_size = :householdSize,
                living_space = :livingSpace,
                sub_type = :subType
            WHERE
                id = :id
    """)
    fun update(@BindBean quote: ApartmentData)

    @SqlUpdate("""
        UPDATE quote_house_data
        SET
            ssn = :ssn,
            first_name = :firstName,
            last_name = :lastName,
            street = :street,
            city = :city,
            zip_code = :zipCode,
            household_size = :householdSize,
            living_space = :livingSpace
        WHERE
            id = :id
    """)
    fun update(@BindBean quote: HouseData)
}
