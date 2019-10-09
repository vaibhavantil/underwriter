package com.hedvig.underwriter.web.Dtos

import java.math.BigDecimal
import java.util.*

data class CompleteQuoteResponseDto (
        val id: String?,
        val price: BigDecimal?,
        val reasonQuoteCannotBeCompleted: List<String>?
)
