package com.hedvig.underwriter.service

import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Flag

interface DebtChecker {
    fun checkDebt(ssn: String): Flag
}