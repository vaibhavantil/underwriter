package com.hedvig.underwriter.service.model

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.model.birthDateFromNorwegianSsn
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.model.birthDateStringFromNorwegianSsn
import java.lang.RuntimeException
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

        return when (this) {
            is NorwegianHomeContentsData,
            is NorwegianTravelData -> this.ssn!!.birthDateFromNorwegianSsn().until(dateToday, ChronoUnit.YEARS)
            is SwedishApartmentData,
            is SwedishHouseData -> this.ssn!!.birthDateFromSwedishSsn().until(dateToday, ChronoUnit.YEARS)
            else -> throw RuntimeException("Can't get age from QuoteData: $this")
        }

    }
}
