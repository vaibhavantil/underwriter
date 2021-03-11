package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.Quote

enum class Nationality {
    SWEDEN,
    NORWAY,
    DENMARK;

    companion object {
        fun fromQuote(quote: Quote) = valueOf(quote.market.name)
    }
}
