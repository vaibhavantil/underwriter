package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.util.logging.Masked
import com.neovisionaries.i18n.CountryCode

data class Address(
    @Masked val street: String,
    val coLine: String? = null,
    val postalCode: String,
    val city: String? = null,
    val country: CountryCode
)
