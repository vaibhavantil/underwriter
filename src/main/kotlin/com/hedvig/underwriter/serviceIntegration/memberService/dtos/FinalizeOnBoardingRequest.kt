package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.AddressData
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.model.birthDate
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import java.time.LocalDate

class FinalizeOnBoardingRequest(
    val memberId: String,
    val ssn: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val address: Address?,
    val birthDate: LocalDate?
) {
    companion object {

        fun fromQuote(
            quote: Quote,
            email: String,
            phoneNumber: String? = null
        ): FinalizeOnBoardingRequest =
            when (quote.data) {
                is SwedishHouseData,
                is SwedishApartmentData,
                is NorwegianHomeContentsData,
                is DanishHomeContentsData,
                is DanishAccidentData,
                is DanishTravelData -> {
                    val addressInsurance = quote.data as AddressData
                    val personPolicyHolder = quote.data as PersonPolicyHolder<*>

                    FinalizeOnBoardingRequest(
                        memberId = quote.memberId!!,
                        ssn = personPolicyHolder.ssn,
                        firstName = personPolicyHolder.firstName!!,
                        lastName = personPolicyHolder.lastName!!,
                        email = email,
                        phoneNumber = phoneNumber,
                        address = Address(
                            street = addressInsurance.street!!,
                            city = addressInsurance.city ?: "",
                            zipCode = addressInsurance.zipCode!!,
                            apartmentNo = "",
                            floor = 0
                        ),
                        birthDate = quote.birthDate
                    )
                }
                is NorwegianTravelData ->
                    FinalizeOnBoardingRequest(
                        memberId = quote.memberId!!,
                        ssn = quote.data.ssn,
                        firstName = quote.firstName,
                        lastName = quote.lastName,
                        email = email,
                        phoneNumber = phoneNumber,
                        address = null,
                        birthDate = quote.birthDate
                    )
            }
    }
}
