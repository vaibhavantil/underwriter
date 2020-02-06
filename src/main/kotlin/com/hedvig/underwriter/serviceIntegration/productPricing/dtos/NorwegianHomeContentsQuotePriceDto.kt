package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.birthDateFromNorwegianSsn
import java.time.LocalDate

data class NorwegianHomeContentsQuotePriceDto(
    var birthDate: LocalDate,
    var livingSpace: Int,
    var coinsured: Int,
    var zipCode: String,
    var type: NorwegianHomeContentsType,
    var isStudent: Boolean
) {
    companion object {
        fun from(quote: Quote): NorwegianHomeContentsQuotePriceDto {
            val quoteData = quote.data
            if (quoteData is NorwegianHomeContentsData) {
                return NorwegianHomeContentsQuotePriceDto(
                    birthDate = quoteData.ssn.birthDateFromNorwegianSsn(),
                    livingSpace = quoteData.livingSpace,
                    coinsured = quoteData.coinsured,
                    zipCode = quoteData.zipCode,
                    type = quoteData.type,
                    isStudent = quoteData.isStudent
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }
    }
}
