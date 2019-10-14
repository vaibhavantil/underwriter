package com.hedvig.underwriter.web

import arrow.core.Either
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.web.Dtos.SignQuoteRequest
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
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID, @RequestBody body: SignQuoteRequest): ResponseEntity<Any> {

        return when(val signedQuoteOrError = quoteService.signQuote(completeQuoteId, body)) {
            is Either.Left -> ResponseEntity.status(402).body(signedQuoteOrError.a)
            is Either.Right -> ResponseEntity.status(200).body(signedQuoteOrError.b)
        }
    }
}