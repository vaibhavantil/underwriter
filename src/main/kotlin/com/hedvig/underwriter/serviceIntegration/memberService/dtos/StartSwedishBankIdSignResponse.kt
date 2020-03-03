package com.hedvig.underwriter.serviceIntegration.memberService.dtos

sealed class StartSwedishBankIdSignResponse {
    data class Success(
        val autoStartToken: String
    ): StartSwedishBankIdSignResponse()

    data class Failed(
        val errorMessage: String
    ): StartSwedishBankIdSignResponse()
}
