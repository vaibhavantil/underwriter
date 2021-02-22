package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.Name
import java.time.LocalDate

data class SignQuoteRequest(
    val name: Name?,
    val ssn: String?,
    val startDate: LocalDate?,
    val email: String
)
