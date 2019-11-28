package com.hedvig.underwriter.graphql.type

sealed class QuoteDetails {
    data class ApartmentQuoteDetails(
        val street: String,
        val zipCode: String,
        val householdSize: Int,
        val livingSpace: Int,
        val type: ApartmentType
    ) : QuoteDetails()

    data class HouseQuoteDetails(
        val street: String,
        val zipCode: String,
        val householdSize: Int,
        val livingSpace: Int,
        val ancillarySpace: Int,
        val extraBuildings: List<ExtraBuilding.ExtraBuildingCore>
    ) : QuoteDetails()
}
