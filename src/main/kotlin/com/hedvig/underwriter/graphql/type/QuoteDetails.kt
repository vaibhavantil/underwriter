package com.hedvig.underwriter.graphql.type

sealed class QuoteDetails {
    data class SwedishApartmentQuoteDetails(
        val street: String,
        val zipCode: String,
        val householdSize: Int,
        val livingSpace: Int,
        val type: ApartmentType
    ) : QuoteDetails()

    data class SwedishHouseQuoteDetails(
        val street: String,
        val zipCode: String,
        val householdSize: Int,
        val livingSpace: Int,
        val ancillarySpace: Int,
        val extraBuildings: List<ExtraBuilding.ExtraBuildingCore>,
        val numberOfBathrooms: Int,
        val yearOfConstruction: Int,
        val isSubleted: Boolean
    ) : QuoteDetails()

    data class NorwegianHomeContentsDetails(
        val street: String,
        val zipCode: String,
        val coinsured: Int,
        val livingSpace: Int,
        val isStudent: Boolean,
        val type: NorwegianHomeContentsType
    ) : QuoteDetails()

    data class NorwegianTravelDetails(
        val coinsured: Int
    ) : QuoteDetails()
}
