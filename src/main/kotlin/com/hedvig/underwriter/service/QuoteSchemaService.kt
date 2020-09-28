package com.hedvig.underwriter.service

import com.fasterxml.jackson.databind.JsonNode
import com.hedvig.underwriter.service.model.QuoteSchema
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

interface QuoteSchemaService {
    val logger: Logger
        get() = LoggerFactory.getLogger(QuoteSchemaService::class.simpleName)

    fun getSchemaByQuoteId(quoteId: UUID): JsonNode?
    fun getSchemaWithDataByQuoteId(quoteId: UUID): QuoteSchema?
}

