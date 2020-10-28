package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.service.model.QuoteRequestData

data class CreateDanishHomeContentsInput(
    val street: String,
    val zipCode: String,
    val coInsured: Int,
    val livingSpace: Int,
    val isStudent: Boolean,
    val type: DanishHomeContentsType
) {
    fun toQuoteRequestData() =
        QuoteRequestData.DanishHomeContents(
            street = this.street,
            zipCode = this.zipCode,
            livingSpace = this.livingSpace,
            coInsured = this.coInsured,
            isStudent = this.isStudent,
            subType = this.type
        )
}
