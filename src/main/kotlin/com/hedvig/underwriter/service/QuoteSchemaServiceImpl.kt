package com.hedvig.underwriter.service

import com.fasterxml.jackson.databind.JsonNode
import com.github.victools.jsonschema.generator.SchemaGenerator
import com.hedvig.underwriter.graphql.type.QuoteMapper
import com.hedvig.underwriter.model.ContractType
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
            is SwedishApartmentData -> getSchemaByContractType(ContractType.SWEDISH_APARTMENT)
            is SwedishHouseData -> getSchemaByContractType(ContractType.SWEDISH_HOUSE)
            is NorwegianHomeContentsData -> getSchemaByContractType(ContractType.NORWEGIAN_HOME_CONTENT)
            is NorwegianTravelData -> getSchemaByContractType(ContractType.NORWEGIAN_TRAVEL)
        }
    }

    override fun getSchemaByContractType(contractType: ContractType): JsonNode {
        val dataClass = when (contractType) {
            ContractType.SWEDISH_APARTMENT -> QuoteSchema.SwedishApartment::class.java
            ContractType.SWEDISH_HOUSE -> QuoteSchema.SwedishHouse::class.java
            ContractType.NORWEGIAN_HOME_CONTENT -> QuoteSchema.NorwegianHomeContent::class.java
            ContractType.NORWEGIAN_TRAVEL -> QuoteSchema.NorwegianTravel::class.java
        }
        return schemaGenerator.generateSchema(dataClass)
    }

    override fun getSchemaDataByQuoteId(quoteId: UUID): QuoteSchema? {
        val quote = quoteService.getQuote(quoteId) ?: return null
        return quoteMapper.mapToQuoteSchemaData(quote)
    }
}
