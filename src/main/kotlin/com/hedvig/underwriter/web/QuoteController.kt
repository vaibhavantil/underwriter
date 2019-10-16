package com.hedvig.underwriter.web

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import java.util.UUID
import javax.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/_/v1/quote")
class QuoteController @Autowired constructor(
    val quoteService: QuoteService,
    val memberService: MemberService
) {

    @GetMapping("/{id}")
    fun getQuote(@PathVariable id: UUID): ResponseEntity<Quote> {
        val optionalQuote = quoteService.getQuote(id) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(optionalQuote)
    }

    @PatchMapping("/{id}")
    fun updateQuoteInfo(@PathVariable id: UUID, @RequestBody incompleteQuoteDto: IncompleteQuoteDto): ResponseEntity<IncompleteQuoteDto> {
        quoteService.updateQuote(incompleteQuoteDto, id)
        return ResponseEntity.ok(incompleteQuoteDto)
    }

    @PostMapping("/{completeQuoteId}/sign")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID, @RequestBody body: SignQuoteRequest): ResponseEntity<SignedQuoteResponseDto> {
        val signedQuoteResponseDto = quoteService.signQuote(completeQuoteId, body)
        return ResponseEntity.ok(signedQuoteResponseDto)
    }
}
