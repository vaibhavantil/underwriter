package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.util.Pii
import com.hedvig.underwriter.model.NorwegianHomeContentsType as InternalNorwegianHomeContentsType

data class EditNorwegianHomeContentsInput(
    @Pii val street: String?,
    val zipCode: String?,
    val coInsured: Int?,
    val livingSpace: Int?,
    val isYouth: Boolean?,
    val type: NorwegianHomeContentsType?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.NorwegianHomeContents(
            street = this.street,
            zipCode = this.zipCode,
            livingSpace = this.livingSpace,
            coInsured = this.coInsured,
            subType = when (this.type) {
                NorwegianHomeContentsType.OWN -> InternalNorwegianHomeContentsType.OWN
                NorwegianHomeContentsType.RENT -> InternalNorwegianHomeContentsType.RENT
                null -> null
            },
            isYouth = this.isYouth,
            city = null
        )
}
