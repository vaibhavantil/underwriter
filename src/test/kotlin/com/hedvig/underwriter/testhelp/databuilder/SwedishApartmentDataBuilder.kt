package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import java.util.UUID

data class SwedishApartmentDataBuilder(
    val firstName: String = "",
    val lastName: String = "",
    val ssn: String = "191212121212",
    val email: String = "em@i.l",
    val phoneNumber: String? = null,
    val zipCode: String = "12345"
) : DataBuilder<QuoteData> {
    override fun build(): QuoteData = SwedishApartmentData(
        id = UUID.randomUUID(),
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = phoneNumber,
        birthDate = ssn.birthDateFromSwedishSsn(),
        ssn = ssn,
        city = "",
        householdSize = 1,
        livingSpace = 1,
        street = "",
        subType = ApartmentProductSubType.BRF,
        zipCode = zipCode
    )
}
