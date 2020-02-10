package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class CreateNorwegianTravelInput(
    val coInsured: Int
) {
    fun toQuoteRequestData() =
        QuoteRequestData.NorwegianTravel(
            coInsured = this.coInsured
        )
}
