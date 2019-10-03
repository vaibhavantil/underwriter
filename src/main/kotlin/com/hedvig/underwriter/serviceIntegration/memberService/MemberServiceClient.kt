package com.hedvig.underwriter.serviceIntegration.memberService
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterSignQuoteRequest
import org.springframework.web.bind.annotation.*
import javax.validation.Valid


@Headers("Accept: application/json;charset=utf-8")
@FeignClient(
        name = "memberServiceClient",
        url = "\${hedvig.member-service.url:member-service}")
interface MemberServiceClient {

    @PostMapping("/member/helloHedvig")
    fun createMember(): ResponseEntity<String>

    @GetMapping("/_/person/status/{ssn}")
    fun personStatus(@PathVariable("ssn") ssn: String): ResponseEntity<PersonStatusDto>

    @PostMapping("/_/debt/check/{ssn}")
    fun checkPersonDebt(@PathVariable("ssn") ssn: String): ResponseEntity<Void>

    @PostMapping("/v2/member/sign/underwriter")
    fun signQuote(@Valid @RequestBody underwriterSignQuoteRequest: UnderwriterSignQuoteRequest,
                  @RequestHeader(value = "hedvig.token") memberId: String
    ): ResponseEntity<UnderwriterQuoteSignResponse>

}

