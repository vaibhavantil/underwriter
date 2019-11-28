package com.hedvig.underwriter.graphql.type

data class CreateApartmentInput(
    val street: String,
    val zipCode: String,
    val householdSize: Int,
    val livingSpace: Int,
    val type: ApartmentType
)
