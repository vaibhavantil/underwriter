package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.HomeInsurance
import com.hedvig.underwriter.model.PersonPolicyHolder
import com.hedvig.underwriter.model.Quote

class FinalizeOnBoardingRequest(
    val memberId: String,
    val ssn: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val address: Address
) {
    companion object {

        fun fromQuote(
            quote: Quote,
            email: String,
            phoneNumber: String? = null
        ): FinalizeOnBoardingRequest {
            val homeInsurance = quote.data as HomeInsurance
            val personPolicyHolder = quote.data as PersonPolicyHolder<*>

            return FinalizeOnBoardingRequest(
                memberId = quote.memberId!!,
                ssn = personPolicyHolder.ssn!!,
                firstName = personPolicyHolder.firstName!!,
                lastName = personPolicyHolder.lastName!!,
                email = email,
                phoneNumber = phoneNumber,
                address = Address(
                    street = homeInsurance.street!!,
                    city = homeInsurance.city ?: "",
                    zipCode = homeInsurance.zipCode!!,
                    apartmentNo = "",
                    floor = 0
                )
            )
        }
    }
}
