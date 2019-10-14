package com.hedvig.underwriter.serviceIntegration.memberService

import arrow.core.Either
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.PersonStatusDto
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.web.Dtos.ErrorQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.UnderwriterQuoteSignRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Service
import feign.FeignException
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientResponseException
import java.lang.RuntimeException

@Service
@EnableFeignClients
class MemberServiceImpl @Autowired constructor(val client: MemberServiceClient,
                                               val objectMapper: ObjectMapper): MemberService {

    override fun updateMemberSsn(memberId: Long, request: UpdateSsnRequest) {
        this.client.updateMemberSsn(memberId, request)
    }

    override fun signQuote(memberId: Long, request: UnderwriterQuoteSignRequest): Either<ErrorQuoteResponseDto, UnderwriterQuoteSignResponse> {
        try {
            val response = this.client.signQuote(memberId, request)
            return Either.right(response.body!!)
        } catch (ex: FeignException) {
            if (ex.status() == 402) {
                val error = objectMapper.readValue<ErrorQuoteResponseDto>(ex.contentUTF8())
                return Either.left(error)
            }
        }
        throw RuntimeException("Cannot sign quote")
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