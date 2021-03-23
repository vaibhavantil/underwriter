package com.hedvig.underwriter.web.dtos

import com.hedvig.libs.logging.masking.Masked

data class UnderwriterQuoteSignRequest(
    @Masked val ssn: String
)
