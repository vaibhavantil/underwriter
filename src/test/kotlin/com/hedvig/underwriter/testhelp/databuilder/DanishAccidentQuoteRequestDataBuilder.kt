package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.service.model.QuoteRequestData

data class DanishAccidentQuoteRequestDataBuilder(
    val street: String = "",
    val zipCode: String = "",
    val coInsured: Int = 1,
    val isStudent: Boolean = false
) : DataBuilder<QuoteRequestData.DanishAccident> {
    override fun build() = QuoteRequestData.DanishAccident(
        street = street,
        zipCode = zipCode,
        coInsured = coInsured,
        isStudent = isStudent
    )
}
