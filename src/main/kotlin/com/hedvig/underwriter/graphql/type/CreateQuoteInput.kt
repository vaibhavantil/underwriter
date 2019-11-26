package com.hedvig.underwriter.graphql.type

import java.util.UUID

data class CreateQuoteInput(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val currentInsurer: String?,
    val ssn: String,
    val apartment: CreateApartmentInput?,
    val house: CreateHouseInput?
)
