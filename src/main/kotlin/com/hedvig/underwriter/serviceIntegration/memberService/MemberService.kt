package com.hedvig.underwriter.serviceIntegration.memberService

import arrow.core.Either
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

interface MemberService {
    val logger: Logger
        get() = LoggerFactory.getLogger("MemberService")

    fun createMember(): String

    fun checkPersonDebt(ssn: String)

    fun getPersonStatus(ssn: String): PersonStatusDto

    fun signQuote(
        memberId: Long,
        underwriterQuoteSignRequest: UnderwriterQuoteSignRequest
    ): Either<ErrorResponseDto, UnderwriterQuoteSignResponse>

    fun updateMemberSsn(memberId: Long, request: UpdateSsnRequest)
}
