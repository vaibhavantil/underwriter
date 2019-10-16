package com.hedvig.underwriter.serviceIntegration.memberService

import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import feign.FeignException
import java.lang.RuntimeException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClientResponseException

@Service
@EnableFeignClients
class MemberServiceImpl @Autowired constructor(val client: MemberServiceClient) : MemberService {
    override fun updateMemberSsn(memberId: Long, request: UpdateSsnRequest) {
        this.client.updateMemberSsn(memberId, request)
    }

    override fun signQuote(memberId: Long, request: UnderwriterQuoteSignRequest): UnderwriterQuoteSignResponse {
        val sign = this.client.signQuote(memberId, request).body
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
        return memberId!!.memberId
    }
}
