package com.hedvig.underwriter.serviceIntegration.notificationService

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.model.email
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.model.ssn
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import org.springframework.stereotype.Service

@Service
class NotificationServiceImpl(
    private val client: NotificationServiceClient
) : NotificationService {
    override fun sendQuoteCreatedEvent(quote: Quote) {
        client.quoteCreated(
            QuoteCreatedEvent(
                memberId = quote.memberId!!,
                quoteId = quote.id,
                firstName = quote.firstName,
                lastName = quote.lastName,
                postalCode = when (quote.data) {
                    is SwedishHouseData -> quote.data.zipCode
                    is SwedishApartmentData -> quote.data.zipCode
                    is NorwegianHomeContentsData -> quote.data.zipCode
                    is NorwegianTravelData -> null
                },
                email = quote.email!!,
                ssn = quote.ssn,
                initiatedFrom = quote.initiatedFrom.name,
                attributedTo = quote.attributedTo.name,
                productType = quote.productType.name,
                currentInsurer = quote.currentInsurer,
                price = quote.price,
                currency = quote.currency,
                originatingProductId = quote.originatingProductId
            )
        )
    }
}
