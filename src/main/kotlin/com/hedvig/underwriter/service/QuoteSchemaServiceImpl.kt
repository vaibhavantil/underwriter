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

        return when (quote.data) {
            is SwedishApartmentData -> getSchemaForContract("SWEDISH_APARTMENT")
            is SwedishHouseData -> getSchemaForContract("SWEDISH_HOUSE")
            is NorwegianHomeContentsData -> getSchemaForContract("NORWEGIAN_HOME_CONTENT")
            is NorwegianTravelData -> getSchemaForContract("NORWEGIAN_TRAVEL")
        }
    }

    override fun getSchemaForContract(contractType: String): JsonNode? {
        val dataClass = when (contractType) {
            "SWEDISH_APARTMENT" -> QuoteSchema.SwedishApartment::class.java
            "SWEDISH_HOUSE" -> QuoteSchema.SwedishHouse::class.java
            "NORWEGIAN_HOME_CONTENT" -> QuoteSchema.NorwegianHomeContent::class.java
            "NORWEGIAN_TRAVEL" -> QuoteSchema.NorwegianTravel::class.java
            else -> {
                logger.error("Unable to get schema for contract of type=$contractType")
                return null
            }
        }
        return schemaGenerator.generateSchema(dataClass)
    }

    override fun getSchemaWithDataByQuoteId(quoteId: UUID): QuoteSchema? {
        val quote = quoteService.getQuote(quoteId) ?: return null
        return quoteMapper.mapToQuoteSchemaWithData(quote)
    }
}
