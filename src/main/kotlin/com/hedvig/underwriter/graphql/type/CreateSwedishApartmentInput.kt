package com.hedvig.underwriter.graphql.type

data class CreateSwedishApartmentInput(
    val street: String,
    val zipCode: String,
    val householdSize: Int,
    val livingSpace: Int,
    val type: ApartmentType
)
