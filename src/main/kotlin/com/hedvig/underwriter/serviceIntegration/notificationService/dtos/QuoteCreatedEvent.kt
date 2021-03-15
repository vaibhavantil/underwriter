package com.hedvig.underwriter.serviceIntegration.notificationService.dtos

import com.hedvig.underwriter.util.logging.Masked
import java.math.BigDecimal
import java.util.UUID

data class QuoteCreatedEvent(
    val memberId: String,
    val quoteId: UUID,
    @Masked val firstName: String,
    @Masked val lastName: String,
    @Masked val street: String?,
    val postalCode: String?,
    @Masked val email: String,
    @Masked val ssn: String?,
    val initiatedFrom: String,
    val attributedTo: String,
    val productType: String,
    val insuranceType: String,
    val currentInsurer: String?,
    val price: BigDecimal?,
    val currency: String,
    val originatingProductId: UUID?
)
