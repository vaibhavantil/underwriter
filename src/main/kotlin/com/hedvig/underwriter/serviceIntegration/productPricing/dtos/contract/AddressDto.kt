package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.neovisionaries.i18n.CountryCode

data class AddressDto(
    val street: String,
    val postalCode: String,
    val city: String?,
    val country: CountryCode
) {
    companion object {
        fun from(data: QuoteData) = when (data) {
            is SwedishApartmentData -> AddressDto(
                street = data.street!!,
                postalCode = data.zipCode!!,
                city = data.city,
                country = CountryCode.SE
            )
            is SwedishHouseData -> AddressDto(
                street = data.street!!,
                postalCode = data.zipCode!!,
                city = data.city,
                country = CountryCode.SE
            )
            is NorwegianHomeContentsData -> AddressDto(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                country = CountryCode.NO
            )
            is DanishHomeContentsData -> AddressDto(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                country = CountryCode.DK
            )
            is DanishAccidentData -> AddressDto(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                country = CountryCode.DK
            )
            is DanishTravelData -> AddressDto(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                country = CountryCode.DK
            )
            is NorwegianTravelData -> throw RuntimeException("Cannot create AddressDto from NorwegianTravelData (data=$data)")
        }
    }
}
