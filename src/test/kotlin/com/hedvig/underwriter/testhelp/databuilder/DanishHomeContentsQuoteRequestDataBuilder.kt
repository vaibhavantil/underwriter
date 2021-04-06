package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.service.model.QuoteRequestData

data class DanishHomeContentsQuoteRequestDataBuilder(
    val street: String = "",
    val zipCode: String = "",
    val bbrId: String? = "1234",
    val city: String? = "city",
    val apartment: String? = "3",
    val floor: String? = "2",
    val coInsured: Int = 3,
    val livingSpace: Int = 2,
    val isStudent: Boolean = false,
    val subType: DanishHomeContentsType = DanishHomeContentsType.RENT
) : DataBuilder<QuoteRequestData.DanishHomeContents> {
    override fun build() = QuoteRequestData.DanishHomeContents(
        street = street,
        zipCode = zipCode,
        bbrId = bbrId,
        apartment = apartment,
        city = city,
        livingSpace = livingSpace,
        coInsured = coInsured,
        isStudent = isStudent,
        subType = subType,
        floor = floor
    )
    fun build(
        newStreet: String? = null,
        newZipCode: String? = null,
        newBbrId: String? = null,
        newApartment: String? = null,
        newFloor: String? = null
    ) = QuoteRequestData.DanishHomeContents(
        street = newStreet,
        zipCode = newZipCode ?: zipCode,
        bbrId = newBbrId,
        apartment = newApartment ?: apartment,
        floor = newFloor ?: floor,
        city = city,
        livingSpace = livingSpace,
        coInsured = coInsured,
        isStudent = isStudent,
        subType = subType
    )
}
