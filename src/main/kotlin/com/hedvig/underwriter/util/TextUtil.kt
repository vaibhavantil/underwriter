package com.hedvig.underwriter.util

import com.hedvig.underwriter.model.Market

fun String.maskSsn(market: Market) = when (market) {
    Market.SWEDEN,
    Market.DENMARK -> this.replaceRange(this.length - 4, this.length, "XXXX")
    Market.NORWAY -> this.replaceRange(this.length - 5, this.length, "XXXXX")
}
