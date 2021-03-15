package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.util.logging.Masked

class Address(
    @Masked val street: String,
    val city: String,
    val zipCode: String,
    val apartmentNo: String,
    val floor: Int
)
