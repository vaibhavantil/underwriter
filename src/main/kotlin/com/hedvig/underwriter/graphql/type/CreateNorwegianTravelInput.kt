package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class CreateNorwegianTravelInput(
    val coinsured: Int
) {
    fun toQuoteRequestData() =
        QuoteRequestData.NorwegianTravel(
            coinsured = this.coinsured
        )
}
