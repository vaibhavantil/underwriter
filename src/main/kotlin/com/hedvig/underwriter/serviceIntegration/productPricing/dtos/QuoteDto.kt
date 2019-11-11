package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.ExtraBuilding
import com.hedvig.underwriter.model.HouseData
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
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
    val isComplete: Boolean,
    val originatingProductId: UUID?,
    val signedProductId: UUID?
) {
    companion object {
        fun fromQuote(quote: Quote): QuoteDto {
            when (quote.data) {
                is ApartmentData -> {
                    return QuoteDto(
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
                        isComplete = quote.isComplete,
                        originatingProductId = quote.originatingProductId,
                        signedProductId = quote.signedProductId
                    )
                }

                is HouseData -> {
                    return QuoteDto(
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
                        isComplete = quote.isComplete,
                        originatingProductId = quote.originatingProductId,
                        signedProductId = quote.signedProductId
                    )
                }
            }
        }
    }
}
