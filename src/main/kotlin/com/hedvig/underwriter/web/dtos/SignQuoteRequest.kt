package com.hedvig.underwriter.web.dtos

import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.util.Pii
import java.time.LocalDate

data class SignQuoteRequest(
    @Pii val name: Name?,
    @Pii val ssn: String?,
    val startDate: LocalDate?,
    @Pii val email: String
)
