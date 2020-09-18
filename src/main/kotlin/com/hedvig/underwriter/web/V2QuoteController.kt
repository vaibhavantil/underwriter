package com.hedvig.underwriter.web

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class QuoteType(val name: String, val schema: String)

@RestController
@RequestMapping("/_/v2/quotes")
class V2QuoteController(val objectMapper: ObjectMapper) {

    @GetMapping("types")
    fun getTypes() = listOf<QuoteType>()

    @PostMapping("{quoteName}")
    fun post() {
        // Create quotes
    }

    @GetMapping("{quoteName}/schema")
    fun getSchema(@PathVariable quoteName: String): JsonNode {
        /*
        Alternative schema generation that we decided not to use
        val configBuilder =
            SchemaGeneratorConfigBuilder(SchemaVersion.DRAFT_2019_09, OptionPreset.PLAIN_JSON)

        configBuilder.with(JacksonModule(JacksonOption.RESPECT_JSONPROPERTY_ORDER))
        //configBuilder.forTypesInGeneral().withIdResolver { it.simpleTypeDescription }

        val config = configBuilder.build()
        val generator = SchemaGenerator(config)

        val schemaBuilder = generator.buildMultipleSchemaDefinitions()
        val quoteSE = schemaBuilder.createSchemaReference(QuoteSE::class.java)
        val quoteDK = generator.generateSchema(QuoteDK::class.java)
        val definitons = schemaBuilder.collectDefinitions("components/schemas")
        */

        val jsonSchemaConfig = JsonSchemaConfig.nullableJsonSchemaDraft4().withJsonSchemaDraft(JsonSchemaDraft.DRAFT_07)

        val jsonSchemaGenerator = JsonSchemaGenerator(objectMapper, true, jsonSchemaConfig)

        return jsonSchemaGenerator.generateJsonSchema(QuoteSE::class.java)
    }
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "quoteType")
data class QuoteDK(val id: String, val firstName: String)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "quoteType")
data class QuoteSE(val id: String, val firstName: String, val size: Int, val address: Address)

data class Address(val street: String, val city: String)
