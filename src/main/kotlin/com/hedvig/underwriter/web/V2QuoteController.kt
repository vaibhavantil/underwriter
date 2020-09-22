package com.hedvig.underwriter.web

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig
import com.kjetland.jackson.jsonSchema.JsonSchemaDraft
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

data class QuoteType(val name: String, val schema: String)

@RestController
@RequestMapping("/_/v2/quotes")
class V2QuoteController(
    val quoteService: QuoteService,
    val objectMapper: ObjectMapper
) {

    val jsonSchemaConfig = JsonSchemaConfig
        .html5EnabledSchema()
        .withJsonSchemaDraft(JsonSchemaDraft.DRAFT_07)
    val jsonSchemaGenerator = JsonSchemaGenerator(objectMapper, true, jsonSchemaConfig)

    @GetMapping("types")
    fun getTypes() = listOf<QuoteType>()

    @PostMapping("{quoteName}")
    fun post() {
        // Create quotes
    }

    @GetMapping("{quoteName}/schema")
    fun getSchemaForQuote(@PathVariable quoteName: UUID): JsonNode {
        val quote =
            quoteService.getQuote(quoteName) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Quote not found")

        val dataClass = when (quote.data) {
            is SwedishHouseData -> QuoteRequestData.SwedishHouse::class.java
            is SwedishApartmentData -> QuoteRequestData.SwedishApartment::class.java
            is NorwegianHomeContentsData -> QuoteRequestData.NorwegianHomeContents::class.java
            else -> TODO()
        }

        return jsonSchemaGenerator.generateJsonSchema(dataClass)
    }
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "quoteType")
data class QuoteDK(val id: String, val firstName: String)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "quoteType")
data class QuoteSE(val id: String, val firstName: String, val size: Int, val address: Address)

data class Address(val street: String, val city: String)
