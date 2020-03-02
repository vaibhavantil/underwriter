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
    val norweiganHomeContents: CreateNorwegianHomeContentsInput?,
    val norweiganTravel: CreateNorwegianTravelInput?,
    val dataCollectionId: UUID?
) {
    fun toHouseOrApartmentIncompleteQuoteDto(
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
        this.norweiganHomeContents != null || this.norweiganTravel != null -> this.ssn?.birthDateFromNorwegianSsn()
        else -> null
    },
    ssn = this.ssn,
    productType = this.getProductType(),
    incompleteQuoteData = when {
        this.swedishApartment != null -> this.swedishApartment.toQuoteRequestData()
        this.swedishHouse != null -> this.swedishHouse.toQuoteRequestData()
        this.norweiganHomeContents != null -> this.norweiganHomeContents.toQuoteRequestData()
        this.norweiganTravel != null -> this.norweiganTravel.toQuoteRequestData()
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
        this.apartment?.let {
            ProductType.APARTMENT
        } ?: this.house?.let {
            ProductType.HOUSE
        } ?: ProductType.UNKNOWN
}
