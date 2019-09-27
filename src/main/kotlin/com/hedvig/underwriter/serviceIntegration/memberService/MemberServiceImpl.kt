package com.hedvig.underwriter.serviceIntegration.memberService

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service

@Service
@EnableFeignClients
class MemberServiceImpl @Autowired constructor(val memberServiceClient: MemberServiceClient): MemberService {

    override fun createMember(): String? {
        val memberId = this.memberServiceClient.createMember().body
        if (memberId != null) return memberId
        return null
    }
}