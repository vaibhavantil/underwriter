package com.hedvig.underwriter.web.Dtos

import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import java.util.*

data class IncompleteQuoteResponseDto(
        val id: UUID,
        val productType: ProductType,
        val quoteInitiatedFrom: QuoteInitiatedFrom?
)