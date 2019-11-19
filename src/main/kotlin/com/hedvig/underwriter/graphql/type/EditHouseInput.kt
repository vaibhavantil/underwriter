package com.hedvig.underwriter.graphql.type

data class EditHouseInput(
    val street: String?,
    val zipCode: String?,
    val householdSize: Int?,
    val livingSpace: Int?,
    val ancillarySpace: Int?,
    val extraBuildings: List<ExtraBuildingInput>?
)
