package com.hedvig.underwriter.web.dtos

import java.util.*

data class QuoteRequestFromBackOfficeDto(
    val agreementId: UUID,
    val memberId: String,
    val underwritingGuidelinesBypassedBy: String?
)
