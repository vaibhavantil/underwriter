package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import java.util.UUID

data class IncompleteQuoteResponseDto(
    val id: UUID,
    val productType: ProductType,
    val quoteInitiatedFrom: QuoteInitiatedFrom?
)
