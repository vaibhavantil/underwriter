package com.hedvig.underwriter.graphql.type.depricated

import com.hedvig.underwriter.graphql.type.ApartmentType

@Deprecated("Use EditSwedishApartmentInput")
data class EditApartmentInput(
    val street: String?,
    val zipCode: String?,
    val householdSize: Int?,
    val livingSpace: Int?,
    val type: ApartmentType?
)
