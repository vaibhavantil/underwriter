package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.util.Pii

data class EditDanishAccidentInput(
    @Pii val street: String?,
    val zipCode: String?,
    val coInsured: Int?,
    val isStudent: Boolean?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.DanishAccident(
            street = this.street,
            zipCode = this.zipCode,
            coInsured = this.coInsured,
            isStudent = this.isStudent
        )
}
