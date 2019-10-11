package com.hedvig.underwriter.web.Dtos

import java.math.BigDecimal

data class CompleteQuoteResponseDto (
        val id: String,
        val price: BigDecimal
)
