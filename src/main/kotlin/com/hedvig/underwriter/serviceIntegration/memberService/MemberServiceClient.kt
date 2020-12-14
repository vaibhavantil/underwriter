package com.hedvig.underwriter.serviceIntegration.memberService

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.FinalizeOnBoardingRequest
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.HelloHedvigResponseDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.InternalMember
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsMemberAlreadySignedResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Nationality
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.RedirectAuthenticationResponseError
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import java.util.UUID

@Headers("Accept: application/json;charset=utf-8")
@FeignClient(
    name = "memberServiceClient",
    url = "\${hedvig.member-service.url:member-service}"
)
interface MemberServiceClient {

    @PostMapping("v2/member/helloHedvig")
    fun createMember(): ResponseEntity<HelloHedvigResponseDto>

    @GetMapping("/_/person/status/{ssn}")
    fun personStatus(@PathVariable("ssn") ssn: String): ResponseEntity<PersonStatusDto>

    @PostMapping("/_/debt/check/{ssn}")
    fun checkPersonDebt(@PathVariable("ssn") ssn: String): ResponseEntity<Void>

    @PostMapping("/v2/member/sign/underwriter")
    fun signQuote(
        @RequestHeader(value = "hedvig.token") memberId: Long,
        @RequestBody underwriterQuoteSignRequest: UnderwriterQuoteSignRequest
    ): ResponseEntity<UnderwriterQuoteSignResponse>

    @PostMapping("/_/member/{memberId}/updateSSN")
    fun updateMemberSsn(@PathVariable memberId: Long, @RequestBody request: UpdateSsnRequest)

    @GetMapping("/v2/member/sign/signedSSN")
    fun checkIsSsnAlreadySignedMemberEntity(@RequestHeader ssn: String): IsSsnAlreadySignedMemberResponse

    @GetMapping("/v2/member/sign/signedMember")
    fun checkIsMemberAlreadySignedMemberEntity(@RequestHeader memberId: Long): IsMemberAlreadySignedResponse

    @RequestMapping("/_/member/{memberId}/finalizeOnboarding")
    fun finalizeOnBoarding(
        @PathVariable("memberId") memberId: String,
        @RequestBody req: FinalizeOnBoardingRequest
    ): ResponseEntity<*>

    @PostMapping("_/member/start/sign/{memberId}")
    fun startSign(
        @PathVariable("memberId") memberId: Long,
        @RequestBody request: UnderwriterStartSignSessionRequest
    ): ResponseEntity<UnderwriterStartSignSessionResponse>

    @GetMapping("/_/member/{memberId}")
    fun getMember(
        @PathVariable("memberId") memberId: Long
    ): ResponseEntity<InternalMember>
}


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = UnderwriterStartSignSessionRequest.SwedishBankId::class, name = "SwedishBankId"),
    JsonSubTypes.Type(value = UnderwriterStartSignSessionRequest.BankIdRedirect::class, name = "BankIdRedirect"),
    JsonSubTypes.Type(value = UnderwriterStartSignSessionRequest.SimpleSign::class, name = "SimpleSign")
)
sealed class UnderwriterStartSignSessionRequest {

    abstract val underwriterSessionReference: UUID

    data class SwedishBankId(
        override val underwriterSessionReference: UUID,
        val nationalIdentification: NationalIdentification,
        val ipAddress: String,
        val isSwitching: Boolean
    ) : UnderwriterStartSignSessionRequest()

    data class BankIdRedirect(
        override val underwriterSessionReference: UUID,
        val nationalIdentification: NationalIdentification,
        val successUrl: String,
        val failUrl: String,
        val country: RedirectCountry
    ) : UnderwriterStartSignSessionRequest()

    data class SimpleSign(
        override val underwriterSessionReference: UUID,
        val nationalIdentification: NationalIdentification
    ) : UnderwriterStartSignSessionRequest()
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = UnderwriterStartSignSessionResponse.SwedishBankId::class, name = "SwedishBankId"),
    JsonSubTypes.Type(value = UnderwriterStartSignSessionResponse.BankIdRedirect::class, name = "BankIdRedirect"),
    JsonSubTypes.Type(value = UnderwriterStartSignSessionResponse.SimpleSign::class, name = "SimpleSign")
)
sealed class UnderwriterStartSignSessionResponse {

    abstract val internalErrorMessage: String?

    data class SwedishBankId(
        val autoStartToken: String?,
        override val internalErrorMessage: String? = null
    ) : UnderwriterStartSignSessionResponse()

    data class BankIdRedirect(
        val redirectUrl: String?,
        override val internalErrorMessage: String? = null,
        val errorMessages: List<RedirectAuthenticationResponseError>? = null
    ) : UnderwriterStartSignSessionResponse()

    data class SimpleSign(
        val successfullyStarted: Boolean,
        override val internalErrorMessage: String? = null
    ) : UnderwriterStartSignSessionResponse()
}

data class NationalIdentification(
    val identification: String,
    val nationality: Nationality
)

enum class RedirectCountry {
    NORWAY,
    DENMARK
}
