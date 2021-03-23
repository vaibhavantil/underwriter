package com.hedvig.underwriter.web

import arrow.core.getOrHandle
import com.fasterxml.jackson.databind.JsonNode
import com.hedvig.underwriter.model.ContractType
import com.hedvig.underwriter.service.QuoteSchemaService
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteSchema
import com.hedvig.libs.logging.calls.LogCall
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
    @LogCall
    fun getSchemaByQuoteId(@PathVariable quoteId: UUID): Any {
        return quoteSchemaService.getSchemaByQuoteId(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when getting schema"
            )
        )
    }

    @GetMapping("{quoteId}/data")
    @LogCall
    fun getSchemaDataByQuoteId(@PathVariable quoteId: UUID): Any {
        return quoteSchemaService.getSchemaDataByQuoteId(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when getting schema data"
            )
        )
    }

    @GetMapping("contract/{contractType}")
    @LogCall
    fun getSchemaByContractType(@PathVariable contractType: ContractType): JsonNode {
        return quoteSchemaService.getSchemaByContractType(contractType)
    }

    @PostMapping("{quoteId}/update")
    @LogCall
    fun updateQuoteBySchemaData(
        @PathVariable quoteId: UUID,
        @RequestBody body: QuoteSchema,
        @RequestParam underwritingGuidelinesBypassedBy: String?
    ): Any {
        val quote = quoteService.getQuote(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when updating quote via schema data"
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
    @LogCall
    fun createQuoteForMemberBySchemaData(
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
