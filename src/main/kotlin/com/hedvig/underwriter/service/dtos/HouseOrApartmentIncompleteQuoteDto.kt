package com.hedvig.underwriter.service.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.web.dtos.IncompleteApartmentQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteHouseQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import java.time.LocalDate
import java.util.UUID

data class HouseOrApartmentIncompleteQuoteDto(
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
    ) var incompleteQuoteData: com.hedvig.underwriter.web.dtos.IncompleteQuoteRequestData?,
    val memberId: String?,
    val originatingProductId: UUID?
) {
    companion object {
        fun from(incompleteQuoteDto: IncompleteQuoteDto): HouseOrApartmentIncompleteQuoteDto {
            if (incompleteQuoteDto.incompleteQuoteData == null && incompleteQuoteDto.incompleteHouseQuoteData == null && incompleteQuoteDto.incompleteApartmentQuoteData == null) {
                throw RuntimeException("Cannot create House or Apartment quote data as incompleteQuoteData, incompleteHouseQuoteData and incompleteApartmentQuoteData are all null")
            }

            return HouseOrApartmentIncompleteQuoteDto(
                firstName = incompleteQuoteDto.firstName,
                lastName = incompleteQuoteDto.lastName,
                currentInsurer = incompleteQuoteDto.currentInsurer,
                birthDate = incompleteQuoteDto.birthDate,
                ssn = incompleteQuoteDto.ssn,
                quotingPartner = incompleteQuoteDto.quotingPartner,
                incompleteQuoteData = when {
                    incompleteQuoteDto.incompleteApartmentQuoteData != null -> incompleteQuoteDto.incompleteApartmentQuoteData
                    incompleteQuoteDto.incompleteHouseQuoteData != null -> incompleteQuoteDto.incompleteHouseQuoteData
                    else -> incompleteQuoteDto.incompleteQuoteData
                },
                productType = incompleteQuoteDto.productType,
                memberId = incompleteQuoteDto.memberId,
                originatingProductId = incompleteQuoteDto.originatingProductId
            )
        }
    }
}
