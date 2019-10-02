package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
        JsonSubTypes.Type(value = IncompleteQuoteData.Home::class, name = "home"),
        JsonSubTypes.Type(value = IncompleteQuoteData.House::class, name = "house")
)
sealed class IncompleteQuoteData {
     data class House (
             var street: String?,
             var zipCode: String?,
             var city: String?,
             var livingSpace: Int?,
             var householdSize: Int?
     ): IncompleteQuoteData()

     data class Home(
             var street: String?,
             var city: String?,
             var zipCode: String?,
             var floor: Int?,
             var numberOfRooms: Int?
     ): IncompleteQuoteData()

 }




