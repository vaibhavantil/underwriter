package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import java.util.UUID

data class UnderwriterStartSwedishBankIdSignSessionRequest(
    val underwriterSessionReference: UUID,
    val ssn: String,
    val ipAddress: String,
    val isSwitching: Boolean
)
