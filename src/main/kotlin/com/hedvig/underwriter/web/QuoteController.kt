package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.web.Dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.SignedQuoteResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
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

    @PostMapping("/{completeQuoteId}/signQuote")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID): ResponseEntity<SignedQuoteResponseDto> {
        val signedQuoteResponseDto = quoteService.signQuote(completeQuoteId)
        return ResponseEntity.ok(signedQuoteResponseDto)

    }
}