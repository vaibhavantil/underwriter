package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import java.util.UUID

data class AddAgreementResponse (
    val quoteId: UUID,
    val agreementId: UUID
)
