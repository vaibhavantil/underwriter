package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.Dtos.*
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
    fun createCompleteQuote(@Valid @PathVariable incompleteQuoteId: UUID): ResponseEntity<CompleteQuoteResponseDto> {
        val quote = quoteService.createCompleteQuote(incompleteQuoteId)
        return ResponseEntity.ok(quote)
    }

//    add in a dto here where we will pass email and activeFrom
    @PostMapping("/{completeQuoteId}/signQuote")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID,
                          @RequestBody request: ChooseActiveFromDto)
        : ResponseEntity<SignedQuoteResponseDto> {
        val signedQuoteResponseDto = quoteService.signQuote(completeQuoteId, request)
        return ResponseEntity.ok(signedQuoteResponseDto)

    }
}