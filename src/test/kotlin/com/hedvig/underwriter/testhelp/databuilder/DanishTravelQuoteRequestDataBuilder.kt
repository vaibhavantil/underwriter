package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.service.model.QuoteRequestData

data class DanishTravelQuoteRequestDataBuilder(
    val street: String = "",
    val zipCode: String = "",
    val coInsured: Int = 1,
    val isStudent: Boolean = false
) : DataBuilder<QuoteRequestData.DanishTravel> {
    override fun build() = QuoteRequestData.DanishTravel(
        street = street,
        zipCode = zipCode,
        coInsured = coInsured,
        isStudent = isStudent
    )
}
