package com.hedvig.underwriter.web.Dtos

import java.time.LocalDate
import java.time.ZoneId

data class SignQuoteRequest (
        val name: Name?,
        val startDateWithZone: DateWithZone?,
        val email: String
)

data class DateWithZone (
        val date: LocalDate,
        val timeZone: ZoneId
)

data class Name (
        val firstName: String,
        val lastName: String
)