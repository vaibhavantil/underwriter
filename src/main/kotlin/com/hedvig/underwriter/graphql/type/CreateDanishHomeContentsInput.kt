package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.util.logging.Masked
import com.hedvig.underwriter.model.DanishHomeContentsType as InternalDanishHomeContentsType

data class CreateDanishHomeContentsInput(
    @Masked val street: String,
    val zipCode: String,
    val city: String?,
    @Masked val bbrId: String?,
    @Masked val apartmentNumber: String?,
    val floor: Int?,
    val coInsured: Int,
    val livingSpace: Int,
    @get:JvmName("getIsStudent")
    val isStudent: Boolean,
    val type: DanishHomeContentsType
) {
    fun toQuoteRequestData() =
        QuoteRequestData.DanishHomeContents(
            street = this.street,
            zipCode = this.zipCode,
            bbrId = this.bbrId,
            apartmentNumber = this.apartmentNumber,
            floor = this.floor,
            city = this.city,
            livingSpace = this.livingSpace,
            coInsured = this.coInsured,
            isStudent = this.isStudent,
            subType = when (this.type) {
                DanishHomeContentsType.OWN -> InternalDanishHomeContentsType.OWN
                DanishHomeContentsType.RENT -> InternalDanishHomeContentsType.RENT
            }
        )
}
