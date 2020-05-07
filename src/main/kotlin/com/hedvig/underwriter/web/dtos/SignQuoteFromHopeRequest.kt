package com.hedvig.underwriter.web.dtos

import java.time.LocalDate

class SignQuoteFromHopeRequest(
    val requestedBy: String,
    val activationDate: LocalDate?
    // TODO: Add email from BO, ssn if we need etc etc
)
