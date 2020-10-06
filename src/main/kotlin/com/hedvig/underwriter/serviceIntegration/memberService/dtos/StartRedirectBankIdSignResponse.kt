package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class StartRedirectBankIdSignResponse(
    val redirectUrl: String?,
    val internalErrorMessage: String? = null,
    val errorMessages: List<NorwegianAuthenticationResponseError>? = null
)
