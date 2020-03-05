package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import java.util.UUID

data class CreateContractResponse(
    val quoteId: UUID,
    val agreementId: UUID,
    val contractId: UUID
)
