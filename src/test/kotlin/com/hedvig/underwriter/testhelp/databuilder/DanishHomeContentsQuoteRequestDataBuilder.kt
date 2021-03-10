package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.service.model.QuoteRequestData

data class DanishHomeContentsQuoteRequestDataBuilder(
    val street: String = "",
    val zipCode: String = "",
    val coInsured: Int = 3,
    val livingSpace: Int = 2,
    val isStudent: Boolean = false,
    val subType: DanishHomeContentsType = DanishHomeContentsType.RENT
) : DataBuilder<QuoteRequestData.DanishHomeContents> {
    override fun build() = QuoteRequestData.DanishHomeContents(
        street = street,
        zipCode = zipCode,
        livingSpace = livingSpace,
        coInsured = coInsured,
        isStudent = isStudent,
        subType = subType,
        bbrId = "1234"
    )
}
