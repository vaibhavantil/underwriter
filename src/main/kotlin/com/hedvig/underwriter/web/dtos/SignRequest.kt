package com.hedvig.underwriter.web.dtos

data class SignRequest(
    val referenceToken: String = "",
    val signature: String = "",
    val oscpResponse: String = ""
)
