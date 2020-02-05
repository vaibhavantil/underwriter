package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.graphql.type.depricated.EditApartmentInput
import com.hedvig.underwriter.graphql.type.depricated.EditHouseInput
import java.time.LocalDate
import java.util.UUID

data class EditQuoteInput(
    val id: UUID,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val currentInsurer: String?,
    val ssn: String?,
    val startDate: LocalDate?,
    @Deprecated("Use swedishApartment")
    val apartment: EditApartmentInput?,
    @Deprecated("Use swedishHouse")
    val house: EditHouseInput?,
    val swedishApartment: EditSwedishApartmentInput?,
    val swedishHouse: EditSwedishHouseInput?,
    val norweiganHomeContents: EditNorwegianHomeContentsInput?,
    val norweiganTravel: EditNorwegianTravelInput?,
    val dataCollectionId: UUID?
)
