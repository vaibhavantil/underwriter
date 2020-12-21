package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.service.model.QuoteRequestData

data class NorwegianHomeContentsQuoteRequestDataBuilder(
    val street: String = "",
    val city: String = "",
    val zipCode: String = "",
    val coInsured: Int = 3,
    val livingSpace: Int = 2,
    val isYouth: Boolean = false,
    val type: NorwegianHomeContentsType = NorwegianHomeContentsType.OWN,
    val floor: Int? = null
) : DataBuilder<QuoteRequestData.NorwegianHomeContents> {
    override fun build() = QuoteRequestData.NorwegianHomeContents(
        street = street,
        zipCode = zipCode,
        city = city,
        livingSpace = livingSpace,
        coInsured = coInsured,
        isYouth = isYouth,
        subType = type
    )
}
