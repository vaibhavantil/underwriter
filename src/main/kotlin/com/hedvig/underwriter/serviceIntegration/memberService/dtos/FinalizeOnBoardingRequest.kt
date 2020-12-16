package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.AddressData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.birthDate
import com.hedvig.underwriter.model.phoneNumber
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
            email: String
        ): FinalizeOnBoardingRequest {
            val addressInsurance = quote.data as? AddressData
            val address = addressInsurance?.let {
                Address(
                    street = it.street!!,
                    city = it.city ?: "",
                    zipCode = it.zipCode!!,
                    apartmentNo = "",
                    floor = 0
                )
            }

            val personPolicyHolder = quote.data as PersonPolicyHolder<*>

            return FinalizeOnBoardingRequest(
                memberId = quote.memberId!!,
                ssn = personPolicyHolder.ssn,
                firstName = personPolicyHolder.firstName!!,
                lastName = personPolicyHolder.lastName!!,
                email = email,
                phoneNumber = quote.phoneNumber,
                address = address,
                birthDate = quote.birthDate
            )
        }
    }
}
