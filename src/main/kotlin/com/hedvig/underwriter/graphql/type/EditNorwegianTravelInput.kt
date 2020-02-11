package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class EditNorwegianTravelInput(
    val coInsured: Int?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.NorwegianTravel(
            coInsured = coInsured
        )
}
