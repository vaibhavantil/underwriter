package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = CompleteQuoteData.CompleteHomeData::class, name = "home"),
        JsonSubTypes.Type(value = CompleteQuoteData.CompleteHouseData::class, name = "house")
)
sealed class CompleteQuoteData {
    data class CompleteHouseData (
            val street: String,
            val zipCode: String,
            val city: String,
            val livingSpace: Int,
            val householdSize: Int
    ): CompleteQuoteData()

    data class CompleteHomeData(
            val street: String,
            val zipCode: String,
            val city: String,
            val livingSpace: Int,
            val householdSize: Int
    ): CompleteQuoteData()


    companion object {
        fun of(data: IncompleteQuoteData) : CompleteQuoteData {
            return when (data) {
                is IncompleteHouseData -> CompleteHouseData(data.street!!, data.zipcode!!, data.city!!, data.livingSpace!!, data.householdSize!!)
                is IncompleteHomeData -> CompleteHomeData(data.street!!, data.zipCode!!, data.city!!, data.livingSpace!!, data.householdSize!!)
            }
        }
    }
}