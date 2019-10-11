package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.web.Dtos.SignQuoteRequest
import com.hedvig.underwriter.web.Dtos.SignedQuoteResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/_/v1/quote")
class QuoteController @Autowired constructor(
        val quoteService: QuoteService,
        val memberService: MemberService
) {

    @PostMapping("/{completeQuoteId}/sign")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID, @RequestBody body: SignQuoteRequest): ResponseEntity<SignedQuoteResponseDto> {
        val signedQuoteResponseDto = quoteService.signQuote(completeQuoteId, body)
        return ResponseEntity.ok(signedQuoteResponseDto)
    }
}