package com.hedvig.underwriter.service.model

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.birthDateFromSsn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface PersonPolicyHolder<T : QuoteData> {
    val ssn: String?
    val firstName: String?
    val lastName: String?
    val email: String?

    fun updateName(firstName: String, lastName: String): T

    fun age(): Long {
        val dateToday = LocalDate.now()

        return this.ssn!!.birthDateFromSsn().until(dateToday, ChronoUnit.YEARS)
    }
}
