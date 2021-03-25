package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.libs.logging.masking.Masked

data class AddressMemberService(
    @Masked var street: String? = null,
    var city: String? = null,
    var zipCode: String? = null,
    var apartmentNo: String? = null,
    var floor: Int? = null
)
