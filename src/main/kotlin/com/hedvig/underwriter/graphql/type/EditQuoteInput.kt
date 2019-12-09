package com.hedvig.underwriter.graphql.type

import java.util.UUID

data class EditQuoteInput(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val ssn: String?,
    val apartment: EditApartmentInput?,
    val house: EditHouseInput?
)
