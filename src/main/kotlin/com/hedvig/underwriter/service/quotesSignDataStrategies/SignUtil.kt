package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData

object SignUtil {
    fun areTwoValidNorwegianQuotes(quotes: List<Quote>): Boolean =
        quotes.size == 2 &&
            quotes.any { quote -> quote.data is NorwegianHomeContentsData } &&
            quotes.any { quote -> quote.data is NorwegianTravelData }

    fun areTwoValidDanishQuotes(quotes: List<Quote>): Boolean =
        quotes.size == 2 &&
            (quotes.any { quote -> quote.data is DanishHomeContentsData } &&
                (quotes.any { quote -> quote.data is DanishAccidentData } ||
                    quotes.any { quote -> quote.data is DanishTravelData }))

    fun areThreeValidDanishQuotes(quotes: List<Quote>): Boolean =
        quotes.size == 3 &&
            quotes.any { quote -> quote.data is DanishHomeContentsData } &&
            quotes.any { quote -> quote.data is DanishAccidentData } &&
            quotes.any { quote -> quote.data is DanishTravelData }

    fun requireValidSwedishQuotes(quotes: List<Quote>) {
        require(quotes.isNotEmpty())
        quotes.forEach {
            require(
                it.data is SwedishApartmentData ||
                    it.data is SwedishHouseData
            )
        }
    }
}
