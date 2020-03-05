package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

data class CreateMandateRequest(
    val firstName: String,
    val lastName: String,
    val ssn: String,
    val referenceToken: String,
    val signature: String,
    val oscpResponse: String
)
