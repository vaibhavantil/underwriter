package com.hedvig.underwriter.graphql.type

data class CreateQuoteInput(
    val id: String,
    val firstName: String,
    val lastName: String,
    val currentInsurer: String?,
    val ssn: String,
    val apartment: CreateApartmentInput?,
    val house: CreateHouseInput?
)
