package com.hedvig.underwriter.serviceIntegration.memberService
import feign.Headers
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping

@Headers("Accept: application/json;charset=utf-8")
@FeignClient(
        name = "memberServiceClient",
        url = "\${hedvig.member-service.url:member-service}")
interface MemberServiceClient {

    @PostMapping("/member/helloHedvig")
    fun createMember(): ResponseEntity<String>
}

