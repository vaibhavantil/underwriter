package com.hedvig.underwriter.web

import arrow.core.Either
import arrow.core.extensions.either.applicative.map
import arrow.core.left
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.Dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.ErrorQuoteResponseDto
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
        val memberService: MemberService,
        val productPricingService: ProductPricingService
) {


    @PostMapping("/{incompleteQuoteId}/completeQuote")
    fun createCompleteQuote(@Valid @PathVariable incompleteQuoteId: UUID): ResponseEntity<Any> {

        return when(val quoteOrError = quoteService.createCompleteQuote(incompleteQuoteId)) {
            is Either.Left -> ResponseEntity.status(402).body(quoteOrError.a)
            is Either.Right -> ResponseEntity.status(200).body(quoteOrError.b)
        }
    }

    @PostMapping("/{completeQuoteId}/sign")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID, @RequestBody body: SignQuoteRequest): ResponseEntity<Any> {
        val signedQuoteResponseDto = quoteService.signQuote(completeQuoteId, body)
        return ResponseEntity.ok(signedQuoteResponseDto)
    }
}