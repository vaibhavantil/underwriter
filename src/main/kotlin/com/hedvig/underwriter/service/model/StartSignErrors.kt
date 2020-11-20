package com.hedvig.underwriter.service.model

import com.hedvig.underwriter.web.dtos.ErrorResponseDto

object StartSignErrors {

    val contactChat = StartSignResponse.FailedToStartSign(
        "contact chat",
        "CONTACT_CHAT"
    )

    val variousMemberId = StartSignResponse.FailedToStartSign(
        "creation and signing must be made by the same member",
        "DIFFRENT_MEMBER_ID_ON_QUOTE_AND_SIGN_REQUEST"
    )

    val noMemberIdOnQuote = StartSignResponse.FailedToStartSign(
        "quotes must have member id to be able to sign",
        "NO_MEMBER_ID_ON_QUOTE"
    )

    val targetURLNotProvided =
        StartSignResponse.FailedToStartSign(
            "Bad request: Must provide `successUrl` and `failUrl` when starting norwegian sign",
            "TAGET_URL_NOT_PROVIDED"
        )

    fun fromErrorResponse(error: ErrorResponseDto) =
        StartSignResponse.FailedToStartSign(
            error.errorMessage,
            error.errorCode.name
        )

    fun emptyAuthTokenFromBankId(message: String) =
        StartSignResponse.FailedToStartSign(
            message,
            "EMPTY_AUTH_TOKEN_FROM_BANK_ID"
        )

    val quotesCanNotBeBundled =
        StartSignResponse.FailedToStartSign(
            "Quotes can not be bundled",
            "QUOTES_CAN_NOT_BE_BUNDLED"
        )

    fun emptyRedirectUrlFromBankId(message: String) =
        StartSignResponse.FailedToStartSign(
            message,
            "EMPTY_REDIRECT_URL_FROM_BANK_ID"
        )
}
