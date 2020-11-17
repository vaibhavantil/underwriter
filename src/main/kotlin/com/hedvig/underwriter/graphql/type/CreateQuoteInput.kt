package com.hedvig.underwriter.graphql.type

import com.fasterxml.jackson.annotation.JsonIgnore
import com.hedvig.underwriter.graphql.type.depricated.CreateApartmentInput
import com.hedvig.underwriter.graphql.type.depricated.CreateHouseInput
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.birthDateFromNorwegianSsn
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.util.toStockholmInstant
import java.time.LocalDate
import java.util.UUID

data class CreateQuoteInput(
    val id: UUID,
    val firstName: String,
    val lastName: String,
    val email: String?,
    val currentInsurer: String?,
    val ssn: String?,
    val birthDate: LocalDate?,
    val startDate: LocalDate?,
    @Deprecated("Use swedishApartment")
    val apartment: CreateApartmentInput?,
    @Deprecated("Use swedishHouse")
    val house: CreateHouseInput?,
    val swedishApartment: CreateSwedishApartmentInput?,
    val swedishHouse: CreateSwedishHouseInput?,
    val norwegianHomeContents: CreateNorwegianHomeContentsInput?,
    val norwegianTravel: CreateNorwegianTravelInput?,
    val danishHomeContents: CreateDanishHomeContentsInput?,
    val danishAccident: CreateDanishAccidentInput?,
    val dataCollectionId: UUID?
) {
    fun toQuoteRequest(
        quotingPartner: Partner? = null,
        memberId: String? = null,
        originatingProductId: UUID? = null
    ) = QuoteRequest(
        firstName = this.firstName,
        lastName = this.lastName,
        email = this.email,
        currentInsurer = this.currentInsurer,
        birthDate = this.birthDate ?: when {
            this.swedishApartment != null || this.swedishHouse != null || this.apartment != null || this.house != null -> this.ssn?.birthDateFromSwedishSsn()
            this.norwegianHomeContents != null || this.norwegianTravel != null -> this.ssn?.birthDateFromNorwegianSsn()
            else -> null
        },
        ssn = this.ssn,
        productType = this.getProductType(),
        incompleteQuoteData = when {
            this.swedishApartment != null -> this.swedishApartment.toQuoteRequestData()
            this.swedishHouse != null -> this.swedishHouse.toQuoteRequestData()
            this.norwegianHomeContents != null -> this.norwegianHomeContents.toQuoteRequestData()
            this.norwegianTravel != null -> this.norwegianTravel.toQuoteRequestData()
            this.danishHomeContents != null -> this.danishHomeContents.toQuoteRequestData()
            this.danishAccident != null -> this.danishAccident.toQuoteRequestData()
            this.house != null -> this.house.toQuoteRequestData()
            else -> this.apartment!!.toQuoteRequestData()
        },
        quotingPartner = quotingPartner,
        memberId = memberId,
        originatingProductId = originatingProductId,
        startDate = this.startDate?.atStartOfDay()?.toStockholmInstant(),
        dataCollectionId = this.dataCollectionId
    )

    @JsonIgnore
    fun getProductType(): ProductType =
        when {
            this.apartment != null || this.swedishApartment != null -> ProductType.APARTMENT
            this.house != null || this.swedishHouse != null -> ProductType.HOUSE
            this.norwegianHomeContents != null || this.danishHomeContents != null -> ProductType.HOME_CONTENT
            this.norwegianTravel != null -> ProductType.TRAVEL
            this.danishAccident != null -> ProductType.ACCIDENT
            // There is an `UNKNOWN` but we don't want to use it because then we can't complete the quote
            else -> throw RuntimeException("Could not map `ProductType` on [CreateQuoteInput: $this]")
        }
}
