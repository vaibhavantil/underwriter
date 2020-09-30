package com.hedvig.underwriter.web

import arrow.core.getOrHandle
import com.hedvig.underwriter.service.QuoteSchemaService
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteSchema
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/_/v1/quotes/schema")
class QuoteSchemaController(
    val quoteSchemaService: QuoteSchemaService,
    val quoteService: QuoteService
) {
    @GetMapping("{quoteId}")
    fun getSchemaByQuoteId(@PathVariable quoteId: UUID): Any {
        return quoteSchemaService.getSchemaByQuoteId(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when getting schema"
            )
        )
    }

    @GetMapping("{quoteId}/data")
    fun getSchemaWithDataByQuoteId(@PathVariable quoteId: UUID): Any {
        return quoteSchemaService.getSchemaWithDataByQuoteId(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when getting schema with data"
            )
        )
    }

    @GetMapping("contract/{contractType}")
    fun getSchemaByContractType(@PathVariable contractType: String): Any {
        return quoteSchemaService.getSchemaForContract(contractType) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Unable to get schema for contractType=$contractType"
            )
        )
    }

    @PostMapping("{quoteId}/update")
    fun updateQuoteBySchemaWithData(
        @PathVariable quoteId: UUID,
        @RequestBody body: QuoteSchema,
        @RequestParam underwritingGuidelinesBypassedBy: String?
    ): Any {
        val quote = quoteService.getQuote(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "QuoteNotFound"
            )
        )

        return quoteService.updateQuote(
            quoteRequest = QuoteRequest.from(quote, body),
            id = quoteId,
            underwritingGuidelinesBypassedBy = underwritingGuidelinesBypassedBy
        ).bimap(
            { ResponseEntity.status(422).body(it) },
            { ResponseEntity.status(200).body(it) }
        ).getOrHandle { it }
    }

    @PostMapping("/{memberId}/create")
    fun createQuoteForMemberBySchemaWithData(
        @PathVariable memberId: String,
        @RequestBody body: QuoteSchema,
        @RequestParam underwritingGuidelinesBypassedBy: String?
    ): Any {
        return quoteService.createQuoteForNewContractFromHope(
            quoteRequest = QuoteRequest.from(memberId, body),
            underwritingGuidelinesBypassedBy = underwritingGuidelinesBypassedBy
        ).bimap(
            { ResponseEntity.status(422).body(it) },
            { ResponseEntity.status(200).body(it) }
        ).getOrHandle { it }
    }
}
