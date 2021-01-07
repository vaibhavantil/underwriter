package com.hedvig.underwriter.service

import com.hedvig.underwriter.service.model.PersonPolicyHolder

interface DebtChecker {
    fun passesDebtCheck(completeQuote: PersonPolicyHolder<*>): List<String>
}
