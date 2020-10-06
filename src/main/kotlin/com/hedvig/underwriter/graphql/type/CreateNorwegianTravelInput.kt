package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class CreateNorwegianTravelInput(
    val coInsured: Int,
    @get:JvmName("getIsYouth")
    val isYouth: Boolean
) {
    fun toQuoteRequestData() =
        QuoteRequestData.NorwegianTravel(
            coInsured = this.coInsured,
            isYouth = this.isYouth
        )
}

