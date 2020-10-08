package com.hedvig.underwriter.model

class MarketInfo(
    val market: Market
)

enum class Market {
    SWEDEN,
    NORWAY,
    DENMARK
}
