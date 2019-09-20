package com.hedvig.underwriter.web

import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.persistence.Id

@RestController
@RequestMapping("/_/v1/quote")
class QuoteController @Autowired constructor(
        val quoteService: QuoteService
) {

    @GetMapping("/{id}")
    fun getIncompleteQuote(@PathVariable id: UUID): ResponseEntity<IncompleteQuote> {
        val optionalQuote:Optional<IncompleteQuote> = quoteService.findIncompleteQuoteById(id)

        if (!quoteService.findIncompleteQuoteById(id).isPresent) {
            return ResponseEntity.notFound().build()
        }
        val incompleteQuote:IncompleteQuote = optionalQuote.get()
        return ResponseEntity.ok(incompleteQuote)
    }

    @PostMapping("/create")
    fun createIncompleteQuote(@RequestBody incompleteQuoteDto: IncompleteQuoteDto): ResponseEntity<Void> {
        quoteService.createIncompleteQuote(incompleteQuoteDto)
        return ResponseEntity.accepted().build()
    }

    @PatchMapping("/{id}/update")
    fun updateQuoteInfo(@PathVariable id: UUID, @RequestBody incompleteQuoteDto: IncompleteQuoteDto): ResponseEntity<Void> {
        quoteService.updateIncompleteQuoteData(incompleteQuoteDto, id)
        return ResponseEntity.accepted().build()
    }
}