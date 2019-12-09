package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.birthDateFromSsn
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
        fun from(quote: Quote) : ApartmentQuotePriceDto {
            val quoteData = quote.data
            if (quoteData is ApartmentData) {
                return ApartmentQuotePriceDto(
                    birthDate = quoteData.ssn!!.birthDateFromSsn(),
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
