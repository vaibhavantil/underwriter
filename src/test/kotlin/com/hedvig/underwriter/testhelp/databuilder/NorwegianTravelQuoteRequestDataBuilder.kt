package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.service.model.QuoteRequestData

data class NorwegianTravelQuoteRequestDataBuilder(
    val coInsured: Int = 3,
    val isYouth: Boolean = false
) : DataBuilder<QuoteRequestData.NorwegianTravel> {
    override fun build() = QuoteRequestData.NorwegianTravel(
        coInsured = coInsured,
        isYouth = isYouth
    )
}
