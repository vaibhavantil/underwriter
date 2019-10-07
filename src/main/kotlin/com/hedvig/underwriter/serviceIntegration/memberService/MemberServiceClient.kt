package com.hedvig.underwriter.serviceIntegration.memberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.HelloHedvigResponseDto
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.Dtos.UnderwriterQuoteSignRequest
import org.springframework.web.bind.annotation.*


@Headers("Accept: application/json;charset=utf-8")
@FeignClient(
        name = "memberServiceClient",
        url = "\${hedvig.member-service.url:member-service}")
interface MemberServiceClient {

    @PostMapping("v2/member/helloHedvig")
    fun createMember(): ResponseEntity<HelloHedvigResponseDto>

    @GetMapping("/_/person/status/{ssn}")
    fun personStatus(@PathVariable("ssn") ssn: String): ResponseEntity<PersonStatusDto>

    @PostMapping("/_/debt/check/{ssn}")
    fun checkPersonDebt(@PathVariable("ssn") ssn: String): ResponseEntity<Void>

    @PostMapping("/v2/member/sign/underwriter")
    fun signQuote(@RequestHeader(value = "hedvig.token") memberId: Long,
            @RequestBody underwriterQuoteSignRequest: UnderwriterQuoteSignRequest
    ): ResponseEntity<UnderwriterQuoteSignResponse>

    @PostMapping("/_/member/{memberId}/updateSSN")
    fun updateMemberSsn(@PathVariable memberId: Long, @RequestBody request: UpdateSsnRequest)
}

