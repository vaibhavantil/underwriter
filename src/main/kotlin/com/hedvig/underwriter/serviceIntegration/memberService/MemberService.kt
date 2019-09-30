package com.hedvig.underwriter.serviceIntegration.memberService

import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import org.slf4j.Logger
import org.slf4j.LoggerFactory


interface MemberService {
    fun createMember(): String?

    fun checkPersonDebt(ssn: String)

    fun getPersonStatus(ssn: String): PersonStatusDto
}