package com.hedvig.underwriter.service.exceptions

import com.hedvig.underwriter.web.dtos.ErrorCodes

class ErrorException(val code: ErrorCodes, message: String) : RuntimeException(message)
