package com.hedvig.underwriter.service.model

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.birthDateFromNorwegianSsn
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.web.dtos.QuoteRequestDto
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class QuoteRequest(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val currentInsurer: String?,
    val birthDate: LocalDate?,
    val ssn: String?,
    val quotingPartner: Partner?,
    val productType: ProductType?,
    @field:JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @field:JsonSubTypes(
        JsonSubTypes.Type(value = QuoteRequestData.SwedishApartment::class, name = "apartment"),
        JsonSubTypes.Type(value = QuoteRequestData.SwedishHouse::class, name = "house"),
        JsonSubTypes.Type(value = QuoteRequestData.NorwegianHomeContents::class, name = "norwegianHomeContents"),
        JsonSubTypes.Type(value = QuoteRequestData.NorwegianTravel::class, name = "norwegianTravel")
    ) val incompleteQuoteData: QuoteRequestData?,
    val memberId: String?,
    val originatingProductId: UUID?,
    val startDate: Instant?,
    val dataCollectionId: UUID?
) {
    companion object {
        fun from(quoteRequestDto: QuoteRequestDto): QuoteRequest {
            if (
                quoteRequestDto.incompleteQuoteData == null &&
                quoteRequestDto.incompleteHouseQuoteData == null &&
                quoteRequestDto.incompleteApartmentQuoteData == null &&
                quoteRequestDto.norwegianHomeContentsData == null &&
                quoteRequestDto.norwegianTravelData == null
            ) {
                throw RuntimeException("Cannot create quote data as incompleteQuoteData, incompleteHouseQuoteData and incompleteApartmentQuoteData, norwegianHomeContentsData, norwegianTravelData are all null")
            }

            return QuoteRequest(
                firstName = quoteRequestDto.firstName,
                lastName = quoteRequestDto.lastName,
                email = quoteRequestDto.email,
                currentInsurer = quoteRequestDto.currentInsurer,
                birthDate = quoteRequestDto.birthDate ?: when {
                    quoteRequestDto.incompleteQuoteData != null ||
                        quoteRequestDto.incompleteApartmentQuoteData != null ||
                        quoteRequestDto.incompleteHouseQuoteData != null -> quoteRequestDto.ssn?.birthDateFromSwedishSsn()
                    quoteRequestDto.norwegianHomeContentsData != null ||
                        quoteRequestDto.norwegianTravelData != null -> quoteRequestDto.ssn?.birthDateFromNorwegianSsn()
                    else -> null
                },
                ssn = quoteRequestDto.ssn,
                quotingPartner = quoteRequestDto.quotingPartner,
                incompleteQuoteData = when {
                    quoteRequestDto.incompleteApartmentQuoteData != null -> quoteRequestDto.incompleteApartmentQuoteData
                    quoteRequestDto.incompleteHouseQuoteData != null -> quoteRequestDto.incompleteHouseQuoteData
                    quoteRequestDto.norwegianHomeContentsData != null -> quoteRequestDto.norwegianHomeContentsData
                    quoteRequestDto.norwegianTravelData != null -> quoteRequestDto.norwegianTravelData
                    else -> quoteRequestDto.incompleteQuoteData
                },
                productType = quoteRequestDto.productType,
                memberId = quoteRequestDto.memberId,
                originatingProductId = quoteRequestDto.originatingProductId,
                startDate = quoteRequestDto.startDate,
                dataCollectionId = quoteRequestDto.dataCollectionId
            )
        }
    }
}