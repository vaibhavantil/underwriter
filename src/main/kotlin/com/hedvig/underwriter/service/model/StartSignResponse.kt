package com.hedvig.underwriter.service.model

import java.util.UUID

sealed class StartSignResponse {

    data class SwedishBankIdSession(
        val session: UUID,
        val autoStartToken: String
    ) : StartSignResponse()

    data class NorwegianBankIdSession(
        val session: UUID,
        val redirectUrl: String
    ) : StartSignResponse()

    data class FailedToStartSign(
        val errorMessage: String
    ) : StartSignResponse()
}
