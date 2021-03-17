package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.model.DanishHomeContentsAddressData
import com.hedvig.underwriter.util.logging.Masked

class Address(
    @Masked val street: String,
    val city: String,
    val zipCode: String,
    val apartmentNo: String,
    val floor: Int
) {
    companion object {
        fun from(danishHomeContentsAddressData: DanishHomeContentsAddressData) = Address(
            street = danishHomeContentsAddressData.street!!,
            city = danishHomeContentsAddressData.city ?: "",
            zipCode = danishHomeContentsAddressData.zipCode!!,
            apartmentNo = danishHomeContentsAddressData.apartmentNumber ?: "",
            floor = danishHomeContentsAddressData.floor ?: 0
        )
    }
}

