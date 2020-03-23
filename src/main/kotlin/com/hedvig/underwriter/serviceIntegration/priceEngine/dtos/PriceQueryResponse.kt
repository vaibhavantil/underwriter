package com.hedvig.underwriter.serviceIntegration.priceEngine.dtos

import java.math.BigDecimal
import java.util.UUID
import javax.money.MonetaryAmount

data class PriceQueryResponse(
    val queryId: UUID,
    val price: MonetaryAmount
) {
    val priceBigDecimal: BigDecimal
        get() = price.number.numberValueExact(BigDecimal::class.java)
}
