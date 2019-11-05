package com.hedvig.underwriter.web.dtos

import java.time.LocalDate

data class ActivateQuoteRequestDto(
    val activationDate: LocalDate?,
    val terminationDate: LocalDate?
)
