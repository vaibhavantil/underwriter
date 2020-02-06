package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote

// TODO: We should probaby have more data to get a price
data class NorwegianTravelQuotePriceDto(
    var coinsured: Int
) {
    companion object {
        fun from(quote: Quote): NorwegianTravelQuotePriceDto {
            val quoteData = quote.data
            if (quoteData is NorwegianTravelData) {
                return NorwegianTravelQuotePriceDto(
                    coinsured = quoteData.coinsured
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }
    }
}
