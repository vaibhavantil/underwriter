package com.hedvig.underwriter.serviceIntegration.memberService

import com.hedvig.underwriter.serviceIntegration.memberService.dtos.FinalizeOnBoardingRequest
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.HelloHedvigResponseDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.InternalMember
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsMemberAlreadySignedResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterStartSignSessionRequest
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterStartSignSessionResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

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

    @DeleteMapping("/_/member/{memberId}")
    fun deleteMember(
        @PathVariable("memberId") memberId: String
    ): ResponseEntity<*>
}
