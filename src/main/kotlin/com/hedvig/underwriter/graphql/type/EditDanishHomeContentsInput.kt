package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.libs.logging.masking.Masked
import com.hedvig.underwriter.model.DanishHomeContentsType as InternalDanishHomeContentsType

data class EditDanishHomeContentsInput(
    @Masked val street: String?,
    val zipCode: String?,
    @Masked val bbrId: String?,
    val apartment: String?,
    val floor: String?,
    val city: String?,
    val coInsured: Int?,
    val livingSpace: Int?,
    val isStudent: Boolean?,
    val type: DanishHomeContentsType?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.DanishHomeContents(
            street = this.street,
            zipCode = this.zipCode,
            bbrId = this.bbrId,
            apartment = this.apartment,
            floor = this.floor,
            city = this.city,
            livingSpace = this.livingSpace,
            coInsured = this.coInsured,
            subType = when (this.type) {
                DanishHomeContentsType.OWN -> InternalDanishHomeContentsType.OWN
                DanishHomeContentsType.RENT -> InternalDanishHomeContentsType.RENT
                null -> null
            },
            isStudent = this.isStudent
        )
}
