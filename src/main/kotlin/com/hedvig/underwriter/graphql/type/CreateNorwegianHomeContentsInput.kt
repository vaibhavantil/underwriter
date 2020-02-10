package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class CreateNorwegianHomeContentsInput(
    val street: String,
    val zipCode: String,
    val coinsured: Int,
    val livingSpace: Int,
    val isStudent: Boolean,
    val type: NorwegianHomeContentsType
) {
    fun toQuoteRequestData() =
        QuoteRequestData.NorwegianHomeContents(
            street = this.street,
            zipCode = this.zipCode,
            livingSpace = this.livingSpace,
            coinsured = this.coinsured,
            type = com.hedvig.underwriter.model.NorwegianHomeContentsType.valueOf(this.type.name),
            isStudent = this.isStudent,
            city = null
        )
}
