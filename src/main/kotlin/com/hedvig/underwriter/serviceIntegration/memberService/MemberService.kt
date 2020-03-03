package com.hedvig.underwriter.serviceIntegration.memberService

import arrow.core.Either
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.StartNorwegianBankIdSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.StartSwedishBankIdSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

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

    fun isSsnAlreadySignedMemberEntity(ssn: String): IsSsnAlreadySignedMemberResponse

    fun finalizeOnboarding(quote: Quote, email: String, phoneNumber: String? = null)

    fun startSwedishBankIdSignQuotes(
        underwriterSessionReference: UUID
    ): StartSwedishBankIdSignResponse

    fun startNorwegianBankIdSignQuotes(
        underwriterSessionReference: UUID
    ): StartNorwegianBankIdSignResponse
}
