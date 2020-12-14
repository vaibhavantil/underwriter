package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class RedirectAuthenticationResponseError(
    val code: Int,
    val description: String
)
