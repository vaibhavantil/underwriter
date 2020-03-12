package com.hedvig.underwriter.web.dtos

import java.time.LocalDate
import java.util.UUID

data class AddAgreementFromQuoteRequest(
    val quoteId: UUID,
    val contractId: UUID?,
    val activeFrom: LocalDate?,
    val activeTo: LocalDate?,
    val previousAgreementActiveTo: LocalDate?
)
