package com.hedvig.underwriter.service.quotesSignDataStrategies

object StrategyHelper {
    fun createSignData(
        ipAddress: String? = null,
        successUrl: String? = null,
        failUrl: String? = null
    ) = SignData(
        ipAddress, successUrl, failUrl
    )
}
