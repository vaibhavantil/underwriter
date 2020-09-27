package com.hedvig.underwriter.service

import com.fasterxml.jackson.databind.JsonNode
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.hedvig.underwriter.graphql.type.QuoteMapper
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.model.QuoteSchema
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class QuoteSchemaServiceImpl(
    private val quoteService: QuoteService,
    private val quoteMapper: QuoteMapper,
    private val schemaGenerator: SchemaGenerator
) : QuoteSchemaService {
    override fun getSchemaByQuoteId(quoteId: UUID): JsonNode? {
        val quote = quoteService.getQuote(quoteId) ?: return null

        val dataClass = when (quote.data) {
            is SwedishHouseData -> QuoteSchema.SwedishHouse::class.java
            is SwedishApartmentData -> TODO()
            is NorwegianHomeContentsData -> TODO()
            is NorwegianTravelData -> TODO()
        }

        return schemaGenerator.generateSchema(dataClass)
    }

    override fun getSchemaWithDataByQuoteId(quoteId: UUID): QuoteSchema? {
        val quote = quoteService.getQuote(quoteId) ?: return null
        return quoteMapper.mapToQuoteSchemaWithData(quote)
    }
}
