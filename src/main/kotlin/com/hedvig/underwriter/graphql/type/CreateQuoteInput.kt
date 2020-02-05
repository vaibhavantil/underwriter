package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.graphql.type.depricated.CreateApartmentInput
import com.hedvig.underwriter.graphql.type.depricated.CreateHouseInput
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
    @Deprecated("Use swedishApartment")
    val apartment: CreateApartmentInput?,
    @Deprecated("Use swedishHouse")
    val house: CreateHouseInput?,
    val swedishApartment: CreateSwedishApartmentInput?,
    val swedishHouse: CreateSwedishHouseInput?,
    val norweiganHomeContents: CreateNorwegianHomeContentsInput?,
    val norweiganTravel: CreateNorwegianTravelInput?,
    val dataCollectionId: UUID?
)
