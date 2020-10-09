package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import java.util.UUID

data class UnderwriterStartRedirectBankIdSignSessionRequest(
    val underwriterSessionReference: UUID,
    val ssn: String,
    val successUrl: String,
    val failUrl: String
)
