package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.model.NorwegianHomeContentsType as InternalNorwegianHomeContentsType

data class CreateNorwegianHomeContentsInput(
    val street: String,
    val zipCode: String,
    val coInsured: Int,
    val livingSpace: Int,
    @get:JvmName("getIsYouth")
    val isYouth: Boolean,
    val type: NorwegianHomeContentsType
) {
    fun toQuoteRequestData() =
        QuoteRequestData.NorwegianHomeContents(
            street = this.street,
            zipCode = this.zipCode,
            livingSpace = this.livingSpace,
            coInsured = this.coInsured,
            isYouth = this.isYouth,
            subType = when (this.type) {
                NorwegianHomeContentsType.OWN -> InternalNorwegianHomeContentsType.OWN
                NorwegianHomeContentsType.RENT -> InternalNorwegianHomeContentsType.RENT
            },
            city = null
        )
}
