package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = IncompleteHomeData::class, name = "home"),
        JsonSubTypes.Type(value = IncompleteHouseData::class, name = "house")
)

sealed class IncompleteQuoteData {
    fun productType():ProductType {
        return when (this) {
            is IncompleteHouseData -> ProductType.HOUSE
            is IncompleteHomeData -> ProductType.HOME
        }
    }

    abstract val householdSize: Int?
    abstract val livingSpace: Int?
}

data class IncompleteHouseData(
        var street: String?,
        var zipcode: String?,
        var city: String?,
        override var livingSpace: Int?,
        override var householdSize: Int?
) : IncompleteQuoteData()

data class IncompleteHomeData(
        val street: String?,
        val city: String?,
        val zipCode: String?,
        override val householdSize: Int?,
        override val livingSpace: Int?,

        @get:JsonProperty(value="isStudent")
        val isStudent: Boolean?
) : IncompleteQuoteData()