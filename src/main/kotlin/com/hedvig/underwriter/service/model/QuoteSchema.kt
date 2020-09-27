package com.hedvig.underwriter.service.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema
import com.hedvig.underwriter.model.ExtraBuildingType

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = QuoteSchema.SwedishHouse::class, name = "SwedishHouse")
)
sealed class QuoteSchema {
    data class SwedishHouse(
        @JsonSchema(title = "Street", required = true)
        val street: String,
        @JsonSchema(title = "Zip Code", required = true, minLength = 5, maxLength = 5)
        val zipCode: String,
        @JsonSchema(title = "City", required = false, description = "The city of dreams")
        val city: String?,
        @JsonSchema(title = "Living Space", required = true, min = 0.0)
        val livingSpace: Int,
        @JsonSchema(title = "Number Co-Insured", required = true)
        val numberCoInsured: Int,
        @JsonSchema(title = "Ancillary Area", required = true)
        val ancillaryArea: Int,
        @JsonSchema(title = "Year Of Construction", required = true)
        val yearOfConstruction: Int,
        @JsonSchema(title = "Number Of Bathrooms", required = true)
        val numberOfBathrooms: Int,
        @field:JsonProperty("subleted")
        @JsonSchema(title = "Is Subleted", required = true)
        val isSubleted: Boolean,
        @JsonSchema(title = "Extra Buildings", required = true)
        val extraBuildings: List<ExtraBuildingSchema>
    ) : QuoteSchema() {
        data class ExtraBuildingSchema(
            @JsonSchema(title = "Type", required = true, defaultValue = "GARAGE")
            val type: ExtraBuildingType,
            @JsonSchema(title = "Area", required = true)
            val area: Int,
            @JsonSchema(title = "Has Water Connected", required = true)
            val hasWaterConnected: Boolean
        )
    }
}
