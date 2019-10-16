package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.HomeProductSubType
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteData

data class PostIncompleteQuoteRequest(
    val productType: ProductType,
    val homeProductSubType: HomeProductSubType?,
    val ssn: String?,
    val quoteDataDto: QuoteData?,
    val partnerName: String?
)
