package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ExtraBuilding
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishHouseData
import java.time.LocalDate
import java.util.UUID

data class SwedishHouseDataBuilder(
    val id: UUID = UUID.fromString("ab5924e4-0c72-11ea-a337-4865ee119be4"),
    val ssn: String? = "191212121212",
    val birthDate: LocalDate? = LocalDate.of(1912, 12, 12),
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String? = "em@i.l",
    val phoneNumber: String? = null,
    val street: String = "",
    val city: String = "",
    val zipCode: String = "",
    val householdSize: Int = 3,
    val livingSpace: Int = 2,
    val ancillaryArea: Int = 50,
    val yearOfConstruction: Int = 1925,
    val numberOfBathrooms: Int = 1,
    val extraBuildings: List<ExtraBuilding> = emptyList(),
    val isSubleted: Boolean = false,
    val internalId: Int? = null
) : DataBuilder<QuoteData> {

    override fun build() = SwedishHouseData(
        id,
        ssn,
        birthDate,
        firstName,
        lastName,
        email,
        phoneNumber,
        street,
        zipCode,
        city,
        livingSpace,
        householdSize,
        ancillaryArea,
        yearOfConstruction,
        numberOfBathrooms,
        extraBuildings,
        isSubleted,
        null,
        internalId
    )
}
