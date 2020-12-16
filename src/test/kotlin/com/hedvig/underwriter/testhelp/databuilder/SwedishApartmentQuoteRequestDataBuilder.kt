package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.service.model.QuoteRequestData

data class
SwedishApartmentQuoteRequestDataBuilder(
    val street: String = "",
    val city: String = "",
    val zipCode: String = "",
    val householdSize: Int = 3,
    val livingSpace: Int = 2,
    val subType: ApartmentProductSubType = ApartmentProductSubType.BRF,
    val floor: Int? = null
) : DataBuilder<QuoteRequestData.SwedishApartment> {
    override fun build() = QuoteRequestData.SwedishApartment(
        street = street,
        zipCode = zipCode,
        city = city,
        livingSpace = livingSpace,
        householdSize = householdSize,
        floor = floor,
        subType = subType
    )
}
