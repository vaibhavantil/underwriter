package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.Quote
import javax.money.MonetaryAmount

data class SignedQuoteRequest(
    val price: MonetaryAmount,
    val email: String,
    val quote: Quote,
    val referenceToken: String,
    val signature: String,
    val oscpResponse: String
)
