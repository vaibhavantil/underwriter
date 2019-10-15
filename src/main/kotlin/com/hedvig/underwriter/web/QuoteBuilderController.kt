package com.hedvig.underwriter.web

import arrow.core.Either
import com.hedvig.underwriter.service.QuoteService

import com.hedvig.underwriter.web.dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@RestController
@RequestMapping("/_/v1/incompleteQuote")
class QuoteBuilderController @Autowired constructor(
        val quoteService: QuoteService
) {
    @PostMapping("/")
    fun createIncompleteQuote(@Valid @RequestBody incompleteQuoteDto: PostIncompleteQuoteRequest): ResponseEntity<IncompleteQuoteResponseDto> {
        val quote = quoteService.createQuote(incompleteQuoteDto)
        return ResponseEntity.ok(quote)
    }


    @PostMapping("/{incompleteQuoteId}/completeQuote")
    fun createCompleteQuote(@Valid @PathVariable incompleteQuoteId: UUID): ResponseEntity<Any> {

        return when(val quoteOrError = quoteService.completeQuote(incompleteQuoteId)) {
            is Either.Left -> ResponseEntity.status(402).body(quoteOrError.a)
            is Either.Right -> ResponseEntity.status(200).body(quoteOrError.b)
        }
    }
}