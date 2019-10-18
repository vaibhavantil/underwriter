package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class UnderwriterQuoteSignResponse(
    val signId: Long,
    val memberIsSigned: Boolean
)
