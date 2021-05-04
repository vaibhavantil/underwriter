package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.libs.logging.masking.Masked

class CreateDanishAccidentInput(
    @Masked val street: String,
    val zipCode: String,
    @Masked val bbrId: String?,
    val apartment: String?,
    val floor: String?,
    val city: String?,
    val coInsured: Int,
    @get:JvmName("getIsStudent")
    val isStudent: Boolean
) {
    fun toQuoteRequestData() =
        QuoteRequestData.DanishAccident(
            street = this.street,
            zipCode = this.zipCode,
            bbrId = this.bbrId,
            apartment = this.apartment,
            floor = this.floor,
            city = this.city,
            coInsured = this.coInsured,
            isStudent = this.isStudent
        )
}
