package com.hedvig.underwriter.model

import java.time.LocalDate
import java.time.ZoneId

data class DateWithZone(
    val date: LocalDate,
    val timeZone: ZoneId
)

data class Name(
    val firstName: String,
    val lastName: String
)
