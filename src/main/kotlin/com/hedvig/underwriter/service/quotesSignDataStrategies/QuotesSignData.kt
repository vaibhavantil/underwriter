package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote

sealed class QuotesSignData {

    data class SwedishBankId(
        val memberId: String,
        val ssn: String,
        val isSwitching: Boolean,
        val quotes: List<Quote>,
        val ipAddress: String?
    ) : QuotesSignData()

    data class NorwegianBankId(
        val memberId: String,
        val ssn: String,
        val quotes: List<Quote>
    ) : QuotesSignData()

    data class DanishBankId(
        val memberId: String,
        val ssn: String,
        val quotes: List<Quote>
    ) : QuotesSignData()

    object CanNotBeBundled : QuotesSignData()
}
