package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import java.time.LocalDate

data class ApartmentQuotePriceDto(
    var birthDate: LocalDate,
    var livingSpace: Int,
    var houseHoldSize: Int,
    var zipCode: String,
    var houseType: ApartmentProductSubType,
    var isStudent: Boolean
) {
    companion object {
        fun from(quote: Quote): ApartmentQuotePriceDto {
            val quoteData = quote.data
            if (quoteData is SwedishApartmentData) {
                return ApartmentQuotePriceDto(
                    birthDate = quoteData.birthDate ?: quoteData.ssn!!.birthDateFromSwedishSsn(),
                    livingSpace = quoteData.livingSpace!!,
                    houseHoldSize = quoteData.householdSize!!,
                    zipCode = quoteData.zipCode!!,
                    houseType = quoteData.subType!!,
                    isStudent = quoteData.isStudent
                )
            }
            throw RuntimeException("missing data cannot create home quote price dto")
        }
    }
}
