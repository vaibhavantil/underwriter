package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import java.time.LocalDate
import java.util.UUID

data class ApartmentDataBuilder(
    val id: UUID = UUID.fromString("ab5924e4-0c72-11ea-a337-4865ee119be4"),
    val ssn: String? = "191212121212",
    val birthDate: LocalDate? = LocalDate.of(1912, 12, 12),
    val firstName: String? = "",
    val lastName: String? = "",
    val email: String? = "em@i.l",
    val phoneNumber: String? = null,
    val street: String? = "",
    val city: String? = "",
    val zipCode: String? = "",
    val householdSize: Int? = 3,
    val livingSpace: Int? = 2,
    val subType: ApartmentProductSubType? = ApartmentProductSubType.BRF,
    val internalId: Int? = null
) : DataBuilder<QuoteData> {

    override fun build() = SwedishApartmentData(
        id,
        ssn,
        birthDate,
        firstName,
        lastName,
        email,
        phoneNumber,
        street,
        city,
        zipCode,
        householdSize,
        livingSpace,
        subType,
        internalId
    )
}
