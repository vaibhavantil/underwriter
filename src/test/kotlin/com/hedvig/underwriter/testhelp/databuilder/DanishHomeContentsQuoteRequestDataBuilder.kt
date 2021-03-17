package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.service.model.QuoteRequestData

data class DanishHomeContentsQuoteRequestDataBuilder(
    val street: String = "",
    val zipCode: String = "",
    val bbrId: String? = "1234",
    val buildingNumber: String? = "14",
    val floor: Int? = 3,
    val city: String? = "test postal code name",
    val coInsured: Int = 3,
    val livingSpace: Int = 2,
    val isStudent: Boolean = false,
    val subType: DanishHomeContentsType = DanishHomeContentsType.RENT
) : DataBuilder<QuoteRequestData.DanishHomeContents> {
    override fun build() = QuoteRequestData.DanishHomeContents(
        street = street,
        zipCode = zipCode,
        bbrId = bbrId,
        livingSpace = livingSpace,
        coInsured = coInsured,
        isStudent = isStudent,
        subType = subType,
        apartmentNumber = buildingNumber,
        floor = floor,
        city = city
    )
    fun build(newStreet: String?, newZipCode: String?, newBbrId: String?) = QuoteRequestData.DanishHomeContents(
        street = newStreet,
        zipCode = newZipCode,
        bbrId = newBbrId,
        livingSpace = livingSpace,
        coInsured = coInsured,
        isStudent = isStudent,
        subType = subType,
        apartmentNumber = buildingNumber,
        floor = floor,
        city = city
    )
}
