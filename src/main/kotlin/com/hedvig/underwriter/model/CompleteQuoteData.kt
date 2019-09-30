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
            val zipcode: String,
            val city: String,
            val livingSpace: Int,
            val householdSize: Int
    ): CompleteQuoteData()

    data class Home(
            val address: String,
            val numberOfRooms: Int,
            val zipCode: String,
            val floor: Int
    ): CompleteQuoteData()
}