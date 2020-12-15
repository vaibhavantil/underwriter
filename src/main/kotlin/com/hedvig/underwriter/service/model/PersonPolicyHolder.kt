package com.hedvig.underwriter.service.model

import com.hedvig.underwriter.model.QuoteData
import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface PersonPolicyHolder<T : QuoteData> {
    val ssn: String?
    val birthDate: LocalDate?
    val firstName: String?
    val lastName: String?
    val email: String?
    val phoneNumber: String?

    fun updateName(firstName: String, lastName: String): T

    fun age(): Long {
        val dateToday = LocalDate.now()

        return this.birthDate?.until(dateToday, ChronoUnit.YEARS)
            ?: throw RuntimeException("Can't get age from QuoteData: $this")
    }
}
