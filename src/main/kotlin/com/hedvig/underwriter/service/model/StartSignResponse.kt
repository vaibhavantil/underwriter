package com.hedvig.underwriter.service.model

import java.util.UUID

sealed class StartSignResponse {

    data class SwedishBankIdSession(
        val autoStartToken: String
    ) : StartSignResponse()

    data class NorwegianBankIdSession(
        val redirectUrl: String
    ) : StartSignResponse()

    data class DanishBankIdSession(
        val redirectUrl: String
    ) : StartSignResponse()

    data class SimpleSignSession(
        val id: UUID
    ) : StartSignResponse()

    data class FailedToStartSign(
        val errorMessage: String,
        val errorCode: String
    ) : StartSignResponse()
}
