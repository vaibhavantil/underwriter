package com.hedvig.underwriter.graphql.type

sealed class IncompleteQuoteDetails {
    data class IncompleteApartmentQuoteDetails(
        val street: String?,
        val zipCode: String?,
        val householdSize: Int?,
        val livingSpace: Int?,
        val type: ApartmentType?
    ) : IncompleteQuoteDetails()

    data class IncompleteHouseQuoteDetails(
        val street: String?,
        val zipCode: String?,
        val householdSize: Int?,
        val livingSpace: Int?,
        val ancillarySpace: Int?,
        val extraBuildings: List<ExtraBuilding.ExtraBuildingCore>?
    ) : IncompleteQuoteDetails()
}
