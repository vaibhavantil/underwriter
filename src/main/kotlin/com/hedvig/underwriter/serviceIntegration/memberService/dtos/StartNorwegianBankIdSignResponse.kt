package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class StartNorwegianBankIdSignResponse(
    val redirectUrl: String?,
    val internalErrorMessage: String? = null,
    val errorMessages: List<NorwegianAuthenticationResponseError>? = null
)

data class StartNorwegianBankIdSignResponse(
    val redirectUrl: String?,
    val internalErrorMessage: String? = null,
    val errorMessages: List<NorwegianAuthenticationResponseError>? = null
)
