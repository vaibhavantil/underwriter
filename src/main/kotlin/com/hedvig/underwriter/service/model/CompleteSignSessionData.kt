package com.hedvig.underwriter.service.model

sealed class CompleteSignSessionData {

    data class SwedishBankIdDataComplete(
        val referenceToken: String,
        val signature: String,
        val oscpResponse: String
    ) : CompleteSignSessionData()

    object NoExtraDataNeeded : CompleteSignSessionData()
}
