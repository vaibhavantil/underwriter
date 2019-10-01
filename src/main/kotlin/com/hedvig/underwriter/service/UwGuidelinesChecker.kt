package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface UwGuidelinesChecker {
    val logger: Logger
        get() = LoggerFactory.getLogger("UnderwriterGuidelinesChecker")

    fun meetsHomeUwGuidelines(completeQuote: CompleteQuote): Boolean

    fun meetsHouseUwGuidelines(completeQuote: CompleteQuote): Boolean
}