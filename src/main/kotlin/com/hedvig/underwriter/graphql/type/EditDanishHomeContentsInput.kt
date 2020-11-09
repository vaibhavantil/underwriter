package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.model.DanishHomeContentsType as InternalDanishHomeContentsType

data class EditDanishHomeContentsInput(
    val street: String?,
    val zipCode: String?,
    val coInsured: Int?,
    val livingSpace: Int?,
    val isStudent: Boolean?,
    val type: DanishHomeContentsType?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.DanishHomeContents(
            street = this.street,
            zipCode = this.zipCode,
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
