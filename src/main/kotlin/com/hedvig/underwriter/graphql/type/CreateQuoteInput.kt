package com.hedvig.underwriter.graphql.type

import java.time.LocalDate
import java.util.UUID

data class CreateQuoteInput(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val currentInsurer: String?,
    val ssn: String,
    val startDate: LocalDate?,
    val apartment: CreateApartmentInput?,
    val house: CreateHouseInput?,
    val swedishApartment: CreateSwedishApartmentInput?,
    val swedishHouse: CreateSwedishHouseInput?,
    val norweiganHomeContents: CreateNorwegianHomeContentsInput?,
    val norweiganTravel: CreateNorwegianTravelInput?,
    val dataCollectionId: UUID?
)
