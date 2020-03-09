package com.hedvig.underwriter.service

sealed class BundledQuotesSign {

    data class SwedishBankId(
        val memberId: String,
        val ssn: String,
        val isSwitching: Boolean
    ): BundledQuotesSign()

    data class NorwegianBankId(
        val memberId: String,
        val ssn: String
    ): BundledQuotesSign()

    object CanNotBeBundled: BundledQuotesSign()
}
