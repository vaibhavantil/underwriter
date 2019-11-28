package com.hedvig.underwriter.graphql.type

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
