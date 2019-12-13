package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import java.time.LocalDate

data class InsuranceCost(
    val monthlyGross: MonetaryAmountV2,
    val monthlyDiscount: MonetaryAmountV2,
    val monthlyNet: MonetaryAmountV2,
    val freeUntil: LocalDate?
)
