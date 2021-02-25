package com.hedvig.underwriter.serviceIntegration.notificationService

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.email
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.model.ssnMaybe
import com.hedvig.underwriter.service.quoteStrategies.QuoteStrategyService
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val client: NotificationServiceClient,
    private val quoteStrategyService: QuoteStrategyService
) : NotificationService {
    override fun sendQuoteCreatedEvent(quote: Quote) {
        val event = quoteStrategyService.createQuoteCreatedEvent(quote)
        client.quoteCreated(event)
    }
}

fun quoteCreatedEvent(
    quote: Quote,
    street: String?,
    postalCode: String?,
    insuranceType: String
): QuoteCreatedEvent {
    return QuoteCreatedEvent(
        memberId = quote.memberId!!,
        quoteId = quote.id,
        firstName = quote.firstName,
        lastName = quote.lastName,
        street = street,
        postalCode = postalCode,
        email = quote.email!!,
        ssn = quote.ssnMaybe,
        initiatedFrom = quote.initiatedFrom.name,
        attributedTo = quote.attributedTo.name,
        productType = quote.productType.name,
        insuranceType = insuranceType,
        currentInsurer = quote.currentInsurer,
        price = quote.price,
        currency = quote.currencyWithFallbackOnMarket,
        originatingProductId = quote.originatingProductId
    )
}
