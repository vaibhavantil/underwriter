package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Flag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DebtCheckerImpl @Autowired constructor (val memberService: MemberService): DebtChecker {
    override fun passesDebtCheck(completeQuote: CompleteQuote): Boolean {
        try {
            memberService.checkPersonDebt(completeQuote.ssn)
            val personStatus = memberService.getPersonStatus(completeQuote.ssn)
            if (personStatus.whitelisted) {
                return true
            }
            if (personStatus.flag == Flag.RED) {
                completeQuote.reasonQuoteCannotBeCompleted += "fails debt check"
                return false
            }
            return personStatus.flag == Flag.GREEN

        } catch(ex: Exception) {
            logger.error("Error getting debt status from member-service", ex)
            return true
        }
    }
}
