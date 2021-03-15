package com.hedvig.underwriter.web.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.util.logging.Masked
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class QuoteRequestDto(
    @Masked val firstName: String?,
    @Masked val lastName: String?,
    @Masked val email: String?,
    val currentInsurer: String?,
    val birthDate: LocalDate?,
    @Masked val ssn: String?,
    val quotingPartner: Partner?,
    val productType: ProductType? = ProductType.UNKNOWN,
    @field:JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @field:JsonSubTypes(
        JsonSubTypes.Type(value = QuoteRequestData.SwedishApartment::class, name = "apartment"),
        JsonSubTypes.Type(value = QuoteRequestData.SwedishHouse::class, name = "house"),
        JsonSubTypes.Type(value = QuoteRequestData.NorwegianHomeContents::class, name = "norwegianHomeContents"),
        JsonSubTypes.Type(value = QuoteRequestData.NorwegianTravel::class, name = "norwegianTravel"),
        JsonSubTypes.Type(value = QuoteRequestData.DanishHomeContents::class, name = "danishHomeContents"),
        JsonSubTypes.Type(value = QuoteRequestData.DanishAccident::class, name = "danishAccident"),
        JsonSubTypes.Type(value = QuoteRequestData.DanishTravel::class, name = "danishTravel")
    )
    val incompleteQuoteData: QuoteRequestData?,
    val incompleteHouseQuoteData: QuoteRequestData.SwedishHouse?,
    val incompleteApartmentQuoteData: QuoteRequestData.SwedishApartment?,
    val norwegianHomeContentsData: QuoteRequestData.NorwegianHomeContents?,
    val norwegianTravelData: QuoteRequestData.NorwegianTravel?,
    val danishHomeContentsData: QuoteRequestData.DanishHomeContents?,
    val danishAccidentData: QuoteRequestData.DanishAccident?,
    val danishTravelData: QuoteRequestData.DanishTravel?,
    val memberId: String? = null,
    val originatingProductId: UUID? = null,
    val startDate: Instant? = null,
    val dataCollectionId: UUID? = null,
    val underwritingGuidelinesBypassedBy: String? = null
)
