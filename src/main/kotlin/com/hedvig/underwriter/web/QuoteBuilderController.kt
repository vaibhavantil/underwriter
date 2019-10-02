package com.hedvig.underwriter.web

import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.service.QuoteBuilderService
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/_/v1/incompleteQuote")
class QuoteBuilderController @Autowired constructor(
        val quoteBuilderService: QuoteBuilderService
) {

    @GetMapping("/{id}")
    fun getIncompleteQuote(@PathVariable id: UUID): ResponseEntity<IncompleteQuote> {
        val optionalQuote:Optional<IncompleteQuote> = quoteBuilderService.findIncompleteQuoteById(id)

        if (!quoteBuilderService.findIncompleteQuoteById(id).isPresent) {
            return ResponseEntity.notFound().build()
        }
        val incompleteQuote:IncompleteQuote = optionalQuote.get()
        return ResponseEntity.ok(incompleteQuote)
    }

    @PostMapping("/buildQuote")
    fun createIncompleteQuote(@RequestBody incompleteQuoteDto: IncompleteQuoteDto): ResponseEntity<IncompleteQuoteResponseDto> {
        val quote = quoteBuilderService.createIncompleteQuote(incompleteQuoteDto)
        return ResponseEntity.ok(quote)
    }

    @PatchMapping("/{id}/update")
    fun updateQuoteInfo(@PathVariable id: UUID, @RequestBody incompleteQuoteDto: IncompleteQuoteDto): ResponseEntity<IncompleteQuoteDto> {
        quoteBuilderService.updateIncompleteQuoteData(incompleteQuoteDto, id)
        return ResponseEntity.ok(incompleteQuoteDto)
    }
}