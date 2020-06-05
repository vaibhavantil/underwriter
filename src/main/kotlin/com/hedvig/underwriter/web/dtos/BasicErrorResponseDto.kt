package com.hedvig.underwriter.web.dtos

interface BasicErrorResponseDto {
    val errorCode: ErrorCodes
    val errorMessage: String
}
