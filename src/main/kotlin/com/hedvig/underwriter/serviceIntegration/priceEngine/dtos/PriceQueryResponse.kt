package com.hedvig.underwriter.serviceIntegration.priceEngine.dtos

import java.math.BigDecimal
import java.util.UUID
import javax.money.MonetaryAmount

data class PriceQueryResponse(
    val queryId: UUID,
    val price: MonetaryAmount,
    val lineItems: List<LineItem>? = null
)

class LineItem(
    val type: String,
    val subType: String,
    val amount: BigDecimal
)
