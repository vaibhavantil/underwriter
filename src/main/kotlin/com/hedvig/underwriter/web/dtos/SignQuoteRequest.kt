package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.util.Masked
import java.time.LocalDate

data class SignQuoteRequest(
    @Masked val name: Name?,
    @Masked val ssn: String?,
    val startDate: LocalDate?,
    @Masked val email: String
)
