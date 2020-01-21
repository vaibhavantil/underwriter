package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.Quote
import java.time.LocalDate
import javax.money.MonetaryAmount

data class SignedQuoteRequest(
    val price: MonetaryAmount,
    val quote: Quote,
    val referenceToken: String,
    val signature: String,
    val oscpResponse: String
)
