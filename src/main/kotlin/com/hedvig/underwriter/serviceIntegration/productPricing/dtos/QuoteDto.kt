package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
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
    val isComplete: Boolean
) {
    companion object {
        fun fromQuoteDto(quote: Quote): QuoteDto {
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
                        isComplete = quote.isComplete
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
                        isComplete = quote.isComplete
                    )
                }
            }
        }
    }
}

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ApartmentData::class, name = "apartment"),
    JsonSubTypes.Type(value = HouseData::class, name = "house")
)
sealed class QuoteData {
    data class ApartmentData(
        val id: UUID,
        val ssn: String? = null,
        val firstName: String? = null,
        val lastName: String? = null,

        val street: String? = null,
        val city: String? = null,
        val zipCode: String? = null,
        val householdSize: Int? = null,
        val livingSpace: Int? = null,

        val subType: ApartmentProductSubType? = null
    ) : QuoteData()

    data class HouseData(
        val id: UUID,
        val ssn: String?,
        val firstName: String?,
        val lastName: String?,

        val street: String?,
        val city: String?,
        val zipCode: String?,
        var householdSize: Int?,
        var livingSpace: Int?
    ) : QuoteData()
}
