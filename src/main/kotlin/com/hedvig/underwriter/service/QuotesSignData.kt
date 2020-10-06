package com.hedvig.underwriter.service

sealed class QuotesSignData {

    data class SwedishBankId(
        val memberId: String,
        val ssn: String,
        val isSwitching: Boolean
    ) : QuotesSignData()

    data class NorwegianBankId(
        val memberId: String,
        val ssn: String
    ) : QuotesSignData()


    data class DanishBankId(
        val memberId: String,
        val ssn: String
    ) : QuotesSignData()

    object CanNotBeBundled : QuotesSignData()
}
