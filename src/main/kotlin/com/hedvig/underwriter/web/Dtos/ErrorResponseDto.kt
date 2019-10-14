package com.hedvig.underwriter.web.Dtos

data class ErrorResponseDto(
        val errorCode: ErrorCodes,
        val errorMessage: String
)

enum class ErrorCodes {
    MEMBER_HAS_EXISTING_INSURANCE,
    MEMBER_BREACHES_UW_GUIDELINES,
    MEMBER_QUOTE_HAS_EXPIRED
}
