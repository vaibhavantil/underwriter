package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote

interface UwGuidelinesChecker {
    fun meetsHomeUwGuidelines(completeQuote: CompleteQuote): Boolean

    fun meetsHouseUwGuidelines(completeQuote: CompleteQuote): Boolean
}