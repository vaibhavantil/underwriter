package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class PersonStatusDto(
    val flag: Flag,
    val whitelisted: Boolean = false
)
