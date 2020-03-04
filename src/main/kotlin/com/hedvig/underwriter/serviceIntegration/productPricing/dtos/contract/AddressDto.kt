package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.neovisionaries.i18n.CountryCode

data class AddressDto(
    val street: String,
    val postalCode: String,
    val city: String?,
    val country: CountryCode
)
