package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class QuoteDto(
    val id: UUID,
    val createdAt: Instant,
    val price: BigDecimal? = null,
    val productType: ProductType,
    val state: QuoteState,
    val initiatedFrom: QuoteInitiatedFrom,
    val attributedTo: Partner,
    val data: com.hedvig.underwriter.model.QuoteData,
    val currentInsurer: String? = null,
    val startDate: LocalDate? = null,
    val validity: Long,
    val memberId: String? = null,
    val breachedUnderwritingGuidelines: List<String>?,
    @get:JsonProperty("isComplete")
    val isComplete: Boolean,
    val originatingProductId: UUID?,
    val signedProductId: UUID?,
    val dataCollectionId: UUID? = null
) {
    companion object {
        fun fromQuote(quote: Quote): QuoteDto {
            return when (quote.data) {
                is SwedishApartmentData -> {
                    QuoteDto(
                        id = quote.id,
                        createdAt = quote.createdAt,
                        price = quote.price,
                        productType = quote.productType,
                        state = quote.state,
                        initiatedFrom = quote.initiatedFrom,
                        attributedTo = quote.attributedTo,
                        data = quote.data,
                        currentInsurer = quote.currentInsurer,
                        startDate = quote.startDate,
                        validity = quote.validity,
                        memberId = quote.memberId,
                        breachedUnderwritingGuidelines = quote.breachedUnderwritingGuidelines,
                        isComplete = quote.isComplete,
                        originatingProductId = quote.originatingProductId,
                        signedProductId = quote.signedProductId,
                        dataCollectionId = quote.dataCollectionId
                    )
                }

                is SwedishHouseData -> {
                    QuoteDto(
                        id = quote.id,
                        createdAt = quote.createdAt,
                        price = quote.price,
                        productType = quote.productType,
                        state = quote.state,
                        initiatedFrom = quote.initiatedFrom,
                        attributedTo = quote.attributedTo,
                        data = quote.data,
                        currentInsurer = quote.currentInsurer,
                        startDate = quote.startDate,
                        validity = quote.validity,
                        memberId = quote.memberId,
                        breachedUnderwritingGuidelines = quote.breachedUnderwritingGuidelines,
                        isComplete = quote.isComplete,
                        originatingProductId = quote.originatingProductId,
                        signedProductId = quote.signedProductId,
                        dataCollectionId = quote.dataCollectionId
                    )
                }
                is NorwegianHomeContentsData -> TODO()
                is NorwegianTravelData -> TODO()
            }
        }
    }
}
