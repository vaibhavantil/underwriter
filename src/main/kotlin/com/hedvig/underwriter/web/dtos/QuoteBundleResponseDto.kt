package com.hedvig.underwriter.web.dtos

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.service.BundleQuotesService
import java.math.BigDecimal

data class QuoteBundleResponseDto(
    val bundleCost: BundleCost
) {
    companion object {
        fun from(quoteBundle: BundleQuotesService.BundledQuotes): QuoteBundleResponseDto {

            val cost = with(quoteBundle.cost) {
                BundleCost(
                    Amount.of(monthlyGross),
                    Amount.of(monthlyDiscount),
                    Amount.of(monthlyNet)
                )
            }
            return QuoteBundleResponseDto(cost)
        }
    }

    data class BundleCost(
        val monthlyGross: Amount,
        val monthlyDiscount: Amount,
        val monthlyNet: Amount
    )

    data class Amount(
        val amount: BigDecimal,
        val currency: String
    ) {
        companion object {
            fun of(amount: MonetaryAmountV2) = Amount(BigDecimal(amount.amount), amount.currency)
        }
    }
}
