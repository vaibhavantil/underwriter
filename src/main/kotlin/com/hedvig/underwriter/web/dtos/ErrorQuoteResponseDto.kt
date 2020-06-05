package com.hedvig.underwriter.web.dtos

data class ErrorQuoteResponseDto(
    override val errorCode: ErrorCodes,
    override val errorMessage: String
) : BasicErrorResponseDto
