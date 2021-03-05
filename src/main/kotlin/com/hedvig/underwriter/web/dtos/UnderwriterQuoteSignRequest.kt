package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.util.Masked

data class UnderwriterQuoteSignRequest(
    @Masked val ssn: String
)
