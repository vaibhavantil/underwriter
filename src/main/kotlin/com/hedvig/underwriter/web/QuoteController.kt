package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
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
class QuoteController @Autowired constructor(val quoteService: QuoteService) {

    @PostMapping("/{incompleteQuoteId}/completeQuote")
    fun createCompleteQuote(@Valid @PathVariable incompleteQuoteId: UUID): ResponseEntity<QuotePriceResponseDto> {
        val quote = quoteService.createCompleteQuote(incompleteQuoteId)
        return ResponseEntity.ok(quote)
    }

    @PostMapping("/{completeQuoteId}/sign")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID) {

    }
}