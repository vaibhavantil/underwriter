package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = SafeQuoteData.HomeData::class, name = "home"),
        JsonSubTypes.Type(value = SafeQuoteData.HouseData::class, name = "house")
)
sealed class SafeQuoteData {
    data class HouseData (
            val street: String,
            val zipCode: String,
            val city: String,
            val livingSpace: Int,
            val householdSize: Int
    ): SafeQuoteData()

    data class HomeData(
            val street: String,
            val zipCode: String,
            val city: String,
            val livingSpace: Int,
            val householdSize: Int
    ): SafeQuoteData()


    companion object
}