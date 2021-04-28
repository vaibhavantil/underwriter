package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.AddressData
import com.hedvig.underwriter.model.DanishHomeContentAddressData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.birthDate
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.model.phoneNumber
import com.hedvig.underwriter.model.ssnMaybe
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
                    street = addressInsurance.street!!,
                    city = addressInsurance.city ?: "",
                    zipCode = addressInsurance.zipCode!!,
                    apartmentNo = (addressInsurance as? DanishHomeContentAddressData)?.apartment ?: "",
                    floor = (addressInsurance as? DanishHomeContentAddressData)?.floor?.toIntOrNull() ?: 0
                )
            }

            return FinalizeOnBoardingRequest(
                memberId = quote.memberId!!,
                ssn = quote.ssnMaybe,
                firstName = quote.firstName,
                lastName = quote.lastName,
                email = email,
                phoneNumber = quote.phoneNumber,
                address = address,
                birthDate = quote.birthDate
            )
        }
    }
}
