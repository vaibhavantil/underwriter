package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class AddressMemberService(
        var street: String? = null,
        var city: String? = null,
        var zipCode: String? = null,
        var apartmentNo: String? = null,
        var floor: Int? = null
)