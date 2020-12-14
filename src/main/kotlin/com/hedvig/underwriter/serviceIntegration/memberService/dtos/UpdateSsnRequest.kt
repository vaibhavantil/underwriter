package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class UpdateSsnRequest(
    val ssn: String,
    val nationality: Nationality
)
