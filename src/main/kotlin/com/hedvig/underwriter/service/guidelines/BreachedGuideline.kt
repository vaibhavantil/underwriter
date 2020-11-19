package com.hedvig.underwriter.service.guidelines

data class BreachedGuideline(
    val message: String,
    val code: String
) {
    override fun toString() = "[code: $code, message: $message]"
}
