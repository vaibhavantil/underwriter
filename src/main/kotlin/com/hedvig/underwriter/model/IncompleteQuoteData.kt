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
             var zipcode: String?,
             var city: String?,
             var livingSpace: Int?,
             var personalNumber: String?,
             var householdSize: Int?
     ): IncompleteQuoteData()

     data class Home(
             var address: String?,
            var numberOfRooms: Int?,
             var zipCode: String?,
             var floor: Int?
     ): IncompleteQuoteData()

 }




