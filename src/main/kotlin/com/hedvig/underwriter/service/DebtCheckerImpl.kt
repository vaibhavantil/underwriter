package com.hedvig.underwriter.service

import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Flag
import com.hedvig.underwriter.utils.Helpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DebtCheckerImpl @Autowired constructor (val memberService: MemberService, val helpers: Helpers): DebtChecker {
    override fun checkDebt(ssn: String): Flag {
        try {
            memberService.checkPersonDebt(ssn)
            val personStatus = memberService.getPersonStatus(ssn)
            if (personStatus.whitelisted) {
                return Flag.GREEN
            }
            return personStatus.flag

        } catch(ex: Exception) {
            helpers.logger.error("Error getting debt status from member-service", ex)
            return Flag.GREEN
        }
    }
}
