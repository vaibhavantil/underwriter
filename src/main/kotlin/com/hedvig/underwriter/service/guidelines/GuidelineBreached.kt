package com.hedvig.underwriter.service.guidelines

data class GuidelineBreached(
    val message: String,
    val code: String
) {
    override fun toString() = "[code: $code, message: $message]"
}
