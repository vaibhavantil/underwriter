package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers

import com.hedvig.productPricingObjects.dtos.Address
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.neovisionaries.i18n.CountryCode

class AddressMapper {
    companion object {
        fun toAddress(data: QuoteData) = when (data) {
            is SwedishApartmentData -> Address(
                street = data.street!!,
                postalCode = data.zipCode!!,
                city = data.city,
                coLine = null,
                country = CountryCode.SE
            )
            is SwedishHouseData -> Address(
                street = data.street!!,
                postalCode = data.zipCode!!,
                city = data.city,
                coLine = null,
                country = CountryCode.SE
            )
            is NorwegianHomeContentsData -> Address(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                coLine = null,
                country = CountryCode.NO
            )
            is DanishHomeContentsData -> Address(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                coLine = null,
                country = CountryCode.DK
            )
            is DanishAccidentData -> Address(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                coLine = null,
                country = CountryCode.DK
            )
            is DanishTravelData -> Address(
                street = data.street,
                postalCode = data.zipCode,
                city = data.city,
                coLine = null,
                country = CountryCode.DK
            )
            is NorwegianTravelData -> throw RuntimeException("Cannot create AddressDto from NorwegianTravelData (data=$data)")
        }
    }
}
