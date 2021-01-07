package com.hedvig.underwriter.service

import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Flag
import com.hedvig.underwriter.util.logger
import org.springframework.stereotype.Service

@Service
class DebtCheckerImpl(val memberService: MemberService) : DebtChecker {
    override fun passesDebtCheck(completeQuote: PersonPolicyHolder<*>): List<String> {
        try {
            memberService.checkPersonDebt(completeQuote.ssn!!)
            val personStatus = memberService.getPersonStatus(completeQuote.ssn!!)
            if (personStatus.whitelisted) {
                return listOf()
            }
            if (personStatus.flag == Flag.RED || personStatus.flag == Flag.AMBER) {
                return listOf("fails debt check")
            }
            return listOf()
        } catch (ex: Exception) {
            logger.error("Error getting debt status from member-service", ex)
            return listOf()
        }
    }
}
