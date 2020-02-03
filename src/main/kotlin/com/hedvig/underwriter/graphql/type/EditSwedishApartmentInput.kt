package com.hedvig.underwriter.graphql.type

data class EditSwedishApartmentInput(
    val street: String?,
    val zipCode: String?,
    val householdSize: Int?,
    val livingSpace: Int?,
    val type: ApartmentType?
)
