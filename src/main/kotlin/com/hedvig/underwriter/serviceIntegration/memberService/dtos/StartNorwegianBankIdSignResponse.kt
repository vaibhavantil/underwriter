package com.hedvig.underwriter.serviceIntegration.memberService.dtos

sealed class StartNorwegianBankIdSignResponse {
    data class Success(
        val redirectUrl: String
    ): StartNorwegianBankIdSignResponse()

    data class Failed(
        val errorMessage: String
    ): StartNorwegianBankIdSignResponse()
}
