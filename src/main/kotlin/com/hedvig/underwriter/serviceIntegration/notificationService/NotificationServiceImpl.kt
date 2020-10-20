package com.hedvig.underwriter.serviceIntegration.notificationService

import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.model.email
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.model.ssnMaybe
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
                street = when (quote.data) {
                    is SwedishHouseData -> quote.data.street
                    is SwedishApartmentData -> quote.data.street
                    is NorwegianHomeContentsData -> quote.data.street
                    is NorwegianTravelData -> null
                    is DanishHomeContentsData -> quote.data.street
                },
                postalCode = when (quote.data) {
                    is SwedishHouseData -> quote.data.zipCode
                    is SwedishApartmentData -> quote.data.zipCode
                    is NorwegianHomeContentsData -> quote.data.zipCode
                    is NorwegianTravelData -> null
                    is DanishHomeContentsData -> quote.data.zipCode
                },
                email = quote.email!!,
                ssn = quote.ssnMaybe,
                initiatedFrom = quote.initiatedFrom.name,
                attributedTo = quote.attributedTo.name,
                productType = quote.productType.name,
                insuranceType = when (quote.data) {
                    is SwedishHouseData -> "HOUSE"
                    is SwedishApartmentData -> quote.data.subType?.name
                    is NorwegianHomeContentsData -> when (quote.data.isYouth) {
                        true -> when (quote.data.type) {
                            NorwegianHomeContentsType.RENT -> "YOUTH_RENT"
                            NorwegianHomeContentsType.OWN -> "YOUTH_OWN"
                        }
                        false -> quote.data.type.name
                    }
                    is NorwegianTravelData -> when (quote.data.isYouth) {
                        true -> "YOUTH"
                        false -> "REGULAR"
                    }
                    is DanishHomeContentsData -> null
                },
                currentInsurer = quote.currentInsurer,
                price = quote.price,
                currency = quote.currency,
                originatingProductId = quote.originatingProductId
            )
        )
    }
}

