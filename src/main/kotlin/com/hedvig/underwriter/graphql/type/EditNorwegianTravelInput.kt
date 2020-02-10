package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class EditNorwegianTravelInput(
    val coinsured: Int?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.NorwegianTravel(
            coinsured = coinsured
        )
}
