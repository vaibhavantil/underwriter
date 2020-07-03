package com.hedvig.underwriter.serviceIntegration.notificationService.dtos

import java.math.BigDecimal
import java.util.UUID

data class QuoteCreatedEvent(
    val memberId: String,
    val quoteId: UUID,
    val firstName: String,
    val lastName: String,
    val postalCode: String?,
    val email: String,
    val ssn: String?,
    val initiatedFrom: String,
    val attributedTo: String,
    val productType: String,
    val currentInsurer: String?,
    val price: BigDecimal?,
    val currency: String,
    val originatingProductId: UUID?
)
