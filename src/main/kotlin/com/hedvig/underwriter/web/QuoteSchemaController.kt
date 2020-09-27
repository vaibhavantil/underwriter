package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteSchemaService
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/_/v2/quotes")
class QuoteSchemaController(
    val quoteSchemaService: QuoteSchemaService
) {
    @GetMapping("{quoteId}/schema")
    fun getSchemaByQuoteId(@PathVariable quoteId: UUID): Any {
        return quoteSchemaService.getSchemaByQuoteId(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when getting schema"
            )
        )
    }

    @GetMapping("{quoteId}/schema/data")
    fun getSchemaWithDataByQuoteId(@PathVariable quoteId: UUID): Any {
        return quoteSchemaService.getSchemaWithDataByQuoteId(quoteId) ?: return ResponseEntity.status(404).body(
            ErrorResponseDto(
                ErrorCodes.NO_SUCH_QUOTE,
                errorMessage = "Quote $quoteId not found when getting schema with data"
            )
        )
    }
}
