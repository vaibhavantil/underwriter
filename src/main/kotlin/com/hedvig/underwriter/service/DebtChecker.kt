package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.PersonPolicyHolder
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface DebtChecker {
    val logger: Logger
        get() = LoggerFactory.getLogger("DebtChecker")

    fun passesDebtCheck(completeQuote: PersonPolicyHolder<*>): List<String>
}