package com.hedvig.underwriter.web

import com.hedvig.underwriter.service.QuoteSchemaService
import com.hedvig.underwriter.service.model.QuoteSchema
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/_/v1/quotes/schema")
class QuoteSchemaController(
    val quoteSchemaService: QuoteSchemaService
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

    @PostMapping("{quoteId}/update")
    fun updateQuoteBySchemaWithData(
        @PathVariable quoteId: UUID,
        @RequestBody schemaWithData: QuoteSchema
    ) {
        LoggerFactory.getLogger("test").info(schemaWithData.toString())
        LoggerFactory.getLogger("test").info(schemaWithData.javaClass.simpleName)
    }
}
