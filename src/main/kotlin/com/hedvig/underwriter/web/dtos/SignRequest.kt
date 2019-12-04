package com.hedvig.underwriter.web.dtos

data class SignRequest(
    val email: String,
    val referenceToken: String,
    val signature: String,
    val oscpResponse: String
)
