package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = CompleteQuoteData.Home::class, name = "home"),
        JsonSubTypes.Type(value = CompleteQuoteData.House::class, name = "house")
)
sealed class CompleteQuoteData {
    data class House (
            val street: String,
            val zipCode: String,
            val city: String,
            val livingSpace: Int,
            val householdSize: Int
    ): CompleteQuoteData()

    data class Home(
            var street: String,
            var city: String,
            var zipCode: String,
            var floor: Int,
            var numberOfRooms: Int
    ): CompleteQuoteData()
}