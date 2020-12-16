package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.QuoteData
import java.time.LocalDate
import java.util.UUID

data class NorwegianHomeContentDataBuilder(
    val id: UUID = UUID.fromString("ab5924e4-0c72-11ea-a337-4865ee119be4"),
    val ssn: String? = "12121200000",
    val birthDate: LocalDate = LocalDate.of(1912, 12, 12),
    val firstName: String = "",
    val lastName: String = "",
    val email: String? = "em@i.l",
    val phoneNumber: String? = null,
    val street: String = "",
    val city: String? = "",
    val zipCode: String = "",
    val coInsured: Int = 3,
    val livingSpace: Int = 2,
    val isYouth: Boolean = false,
    val type: NorwegianHomeContentsType = NorwegianHomeContentsType.OWN,
    val internalId: Int? = null
) : DataBuilder<QuoteData> {

    override fun build() = NorwegianHomeContentsData(
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
        coInsured,
        livingSpace,
        isYouth,
        type,
        internalId
    )
}
