package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.util.logging.Masked
import java.time.LocalDate

data class SignQuoteRequestDto(
    @Masked val name: Name?,
    @Masked val ssn: String?,
    val startDate: LocalDate?,
    @Masked val email: String
)
