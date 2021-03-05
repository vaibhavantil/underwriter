package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.util.Pii

class CreateDanishAccidentInput(
    @Pii val street: String,
    val zipCode: String,
    val coInsured: Int,
    @get:JvmName("getIsStudent")
    val isStudent: Boolean
) {
    fun toQuoteRequestData() =
        QuoteRequestData.DanishAccident(
            street = this.street,
            zipCode = this.zipCode,
            coInsured = this.coInsured,
            isStudent = this.isStudent
        )
}
