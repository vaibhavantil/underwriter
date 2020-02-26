package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.AddressData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.model.PersonPolicyHolder

class FinalizeOnBoardingRequest(
    val memberId: String,
    val ssn: String?,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val address: Address?
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
                is NorwegianHomeContentsData -> {
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
                        )
                    )
                }
                is NorwegianTravelData ->
                    FinalizeOnBoardingRequest(
                        memberId = quote.memberId!!,
                        ssn = quote.data.ssn,
                        firstName = quote.data.firstName!!,
                        lastName = quote.data.lastName!!,
                        email = email,
                        phoneNumber = phoneNumber,
                        address = null
                    )
            }
    }
}
