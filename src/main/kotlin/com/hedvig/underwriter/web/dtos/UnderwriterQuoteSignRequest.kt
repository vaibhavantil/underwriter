package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.util.Pii

data class UnderwriterQuoteSignRequest(
    @Pii val ssn: String
)
