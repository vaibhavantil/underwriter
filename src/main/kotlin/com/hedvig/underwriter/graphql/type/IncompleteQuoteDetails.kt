package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.util.Pii

sealed class IncompleteQuoteDetails {
    data class IncompleteApartmentQuoteDetails(
        @Pii val street: String?,
        val zipCode: String?,
        val householdSize: Int?,
        val livingSpace: Int?,
        val type: ApartmentType?
    ) : IncompleteQuoteDetails()

    data class IncompleteHouseQuoteDetails(
        @Pii val street: String?,
        val zipCode: String?,
        val householdSize: Int?,
        val livingSpace: Int?,
        val ancillarySpace: Int?,
        val extraBuildings: List<ExtraBuilding.ExtraBuildingCore>?,
        val numberOfBathrooms: Int?,
        val yearOfConstruction: Int?,
        val isSubleted: Boolean?
    ) : IncompleteQuoteDetails()
}
