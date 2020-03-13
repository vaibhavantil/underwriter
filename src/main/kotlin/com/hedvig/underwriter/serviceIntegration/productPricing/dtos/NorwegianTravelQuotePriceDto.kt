package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote

data class NorwegianTravelQuotePriceDto(
    var coInsured: Int,
    val isYouth: Boolean
) {
    companion object {
        fun from(quote: Quote): NorwegianTravelQuotePriceDto {
            val quoteData = quote.data
            if (quoteData is NorwegianTravelData) {
                return NorwegianTravelQuotePriceDto(
                    coInsured = quoteData.coInsured,
                    isYouth = quoteData.isYouth
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }
    }
}
