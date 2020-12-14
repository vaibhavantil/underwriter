package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData

enum class Nationality {
    SWEDEN,
    NORWAY,
    DENMARK;

    companion object {
        fun fromQuote(quote: Quote) = when (quote.data) {
            is SwedishHouseData,
            is SwedishApartmentData -> SWEDEN
            is NorwegianHomeContentsData,
            is NorwegianTravelData -> NORWAY
            is DanishHomeContentsData,
            is DanishAccidentData,
            is DanishTravelData -> DENMARK
        }
    }
}
