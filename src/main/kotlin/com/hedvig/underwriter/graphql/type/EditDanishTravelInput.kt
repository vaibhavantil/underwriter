package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData

data class EditDanishTravelInput(
    val street: String?,
    val zipCode: String?,
    val coInsured: Int?,
    val isStudent: Boolean?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.DanishTravel(
            street = this.street,
            zipCode = this.zipCode,
            coInsured = this.coInsured,
            isStudent = this.isStudent
        )
}
