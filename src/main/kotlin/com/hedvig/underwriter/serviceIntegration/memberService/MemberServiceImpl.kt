package com.hedvig.underwriter.serviceIntegration.memberService

import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterSignQuoteRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service
import feign.FeignException
import org.springframework.web.client.RestClientResponseException
import java.lang.RuntimeException

@Service
@EnableFeignClients
class MemberServiceImpl @Autowired constructor(val client: MemberServiceClient): MemberService {

    override fun signQuote(signRequest: UnderwriterSignQuoteRequest, memberId: String):UnderwriterQuoteSignResponse {
        this.client.signQuote(signRequest, memberId)
        val sign = this.client.signQuote(signRequest, memberId).body
            if (sign != null) return sign else throw RuntimeException("Cannot sign quote")
    }

    override fun checkPersonDebt(ssn: String) {
        try {
            this.client.checkPersonDebt(ssn)
        } catch (e: RestClientResponseException) {
            logger.error("Cannot check debt for the following personnummer {}", ssn)
        } catch (e: FeignException) {
            logger.error("Cannot check debt for the following personnummer {}", ssn)
        }
    }

    override fun getPersonStatus(ssn: String): PersonStatusDto {
        val response = this.client.personStatus(ssn)
        return if (response.body != null) response.body!! else throw NullPointerException("person status should not be null")
    }

    override fun createMember(): String {
        val memberId = this.client.createMember().body
        return memberId?.substring(12..20) ?: throw NullPointerException("couldn't create member")
    }
}