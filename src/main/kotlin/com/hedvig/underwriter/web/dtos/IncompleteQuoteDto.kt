package com.hedvig.underwriter.web.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ExtraBuildingRequestDto
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class IncompleteQuoteDto(
    val firstName: String?,
    val lastName: String?,
    val currentInsurer: String?,
    val birthDate: LocalDate?,
    val ssn: String?,
    val quotingPartner: Partner?,
    val productType: ProductType?,
    @field:JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @field:JsonSubTypes(
        JsonSubTypes.Type(value = IncompleteApartmentQuoteDataDto::class, name = "apartment"),
        JsonSubTypes.Type(value = IncompleteHouseQuoteDataDto::class, name = "house")
    ) val incompleteQuoteData: IncompleteQuoteRequestData?,
    val incompleteHouseQuoteData: IncompleteHouseQuoteDataDto?,
    val incompleteApartmentQuoteData: IncompleteApartmentQuoteDataDto?,
    val memberId: String? = null,
    val originatingProductId: UUID? = null,
    val startDate: Instant? = null
)

sealed class IncompleteQuoteRequestData

data class IncompleteHouseQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val ancillaryArea: Int?,
    val yearOfConstruction: Int?,
    val numberOfBathrooms: Int?,
    val extraBuildings: List<ExtraBuildingRequestDto>?,
    @field:JsonProperty("subleted")
    val isSubleted: Boolean?,
    val floor: Int? = 0
) : IncompleteQuoteRequestData()

data class IncompleteApartmentQuoteDataDto(
    val street: String?,
    val zipCode: String?,
    val city: String?,
    val livingSpace: Int?,
    val householdSize: Int?,
    val floor: Int?,
    val subType: ApartmentProductSubType?
) : IncompleteQuoteRequestData()
