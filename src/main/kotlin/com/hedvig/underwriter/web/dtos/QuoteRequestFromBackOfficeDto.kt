package com.hedvig.underwriter.web.dtos

import java.util.UUID

data class QuoteRequestFromBackOfficeDto(
    val agreementId: UUID,
    val memberId: String,
    val underwritingGuidelinesBypassedBy: String?
)
