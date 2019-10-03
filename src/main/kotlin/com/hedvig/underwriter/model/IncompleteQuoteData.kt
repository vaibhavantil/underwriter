package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = Home::class, name = "home"),
        JsonSubTypes.Type(value = House::class, name = "house")
)

sealed class IncompleteQuoteData {
    fun productType():ProductType {
        return when (this) {
            is House -> ProductType.HOUSE
            is Home -> ProductType.HOME
        }
    }

    abstract val householdSize: Int?
    abstract val livingSpace: Int?
//    add abstract vals
}

data class House(
        var street: String?,
        var zipCode: String?,
        var city: String?,
        override var livingSpace: Int?,
        override var householdSize: Int?
) : IncompleteQuoteData()

data class Home(
        val street: String?,
        override val livingSpace: Int?,
        val zipCode: String?,
        val city: String?,
        val floor: Int?,
        override val householdSize: Int?,
        @get:JsonProperty(value="isStudent")
        val isStudent: Boolean?
) : IncompleteQuoteData()