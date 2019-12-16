package com.hedvig.underwriter.graphql.type

import java.time.LocalDate
import java.util.UUID

data class EditQuoteInput(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val ssn: String?,
    val startDate: LocalDate?,
    val apartment: EditApartmentInput?,
    val house: EditHouseInput?
)
