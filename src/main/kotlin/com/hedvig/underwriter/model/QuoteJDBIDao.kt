package com.hedvig.underwriter.model

import org.jdbi.v3.sqlobject.customizer.BindBean
import org.jdbi.v3.sqlobject.statement.SqlUpdate
import java.util.*

interface QuoteJDBIDao {

    @SqlUpdate(
        """
        INSERT INTO quote
            (id, 
            created_at, 
            state, 
            product_type, 
            initiated_from, 
            line_of_business, 
            price,
            first_name,
            last_name,
            current_insurer,
            living_space,
            house_hold_size,
            is_student,
            ssn,
            startDate)
        
        values
        
            (:id, 
            :state, 
            :createdAt, 
            :productType, 
            :initiatedFrom, 
            :lineOfBusiness, 
            :price, 
            :firstName,
            :lastName, 
            :currentInsurer,
            :ssn, 
            :startDate) 
    """
    )
    fun insertQuote(@BindBean quote: Quote)

    fun loadQuote(quote: UUID): Quote?
    fun update(quote: Quote)
}