package com.hedvig.underwriter.graphql.type.depricated

import com.hedvig.underwriter.graphql.type.ExtraBuildingInput

@Deprecated("Use CreateSwedishHouseInput")
data class CreateHouseInput(
    val street: String,
    val zipCode: String,
    val householdSize: Int,
    val livingSpace: Int,
    val ancillarySpace: Int,
    val yearOfConstruction: Int,
    val numberOfBathrooms: Int,
    val isSubleted: Boolean,
    val extraBuildings: List<ExtraBuildingInput>
)
