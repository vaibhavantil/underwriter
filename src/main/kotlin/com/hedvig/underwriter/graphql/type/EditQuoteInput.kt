package com.hedvig.underwriter.graphql.type

data class EditQuoteInput(
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val ssn: String?,
    val apartment: EditApartmentInput?,
    val house: EditHouseInput?
)
