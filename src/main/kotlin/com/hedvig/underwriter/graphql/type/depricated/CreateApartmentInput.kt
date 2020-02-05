package com.hedvig.underwriter.graphql.type.depricated

import com.hedvig.underwriter.graphql.type.ApartmentType

@Deprecated("Use CreateSwedishApartmentInput")
data class CreateApartmentInput(
    val street: String,
    val zipCode: String,
    val householdSize: Int,
    val livingSpace: Int,
    val type: ApartmentType
)
