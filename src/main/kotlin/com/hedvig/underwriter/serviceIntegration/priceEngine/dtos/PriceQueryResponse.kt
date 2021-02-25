package com.hedvig.underwriter.serviceIntegration.priceEngine.dtos

import java.util.UUID
import javax.money.MonetaryAmount

data class PriceQueryResponse(
    val queryId: UUID,
    val price: MonetaryAmount
)
