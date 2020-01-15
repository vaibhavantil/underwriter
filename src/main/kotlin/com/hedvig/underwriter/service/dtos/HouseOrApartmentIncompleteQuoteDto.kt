package com.hedvig.underwriter.service.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.web.dtos.IncompleteApartmentQuoteDataDto
import com.hedvig.underwriter.web.dtos.IncompleteHouseQuoteDataDto
import com.hedvig.underwriter.web.dtos.QuoteRequestDto
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class HouseOrApartmentIncompleteQuoteDto(
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
        JsonSubTypes.Type(value = IncompleteApartmentQuoteDataDto::class, name = "apartment"),
        JsonSubTypes.Type(value = IncompleteHouseQuoteDataDto::class, name = "house")
    ) var incompleteQuoteData: com.hedvig.underwriter.web.dtos.IncompleteQuoteRequestData?,
    val memberId: String?,
    val originatingProductId: UUID?,
    val startDate: Instant?,
    val dataCollectionId: UUID?
) {
    companion object {
        fun from(quoteRequestDto: QuoteRequestDto): HouseOrApartmentIncompleteQuoteDto {
            if (quoteRequestDto.incompleteQuoteData == null && quoteRequestDto.incompleteHouseQuoteData == null && quoteRequestDto.incompleteApartmentQuoteData == null) {
                throw RuntimeException("Cannot create House or Apartment quote data as incompleteQuoteData, incompleteHouseQuoteData and incompleteApartmentQuoteData are all null")
            }

            return HouseOrApartmentIncompleteQuoteDto(
                firstName = quoteRequestDto.firstName,
                lastName = quoteRequestDto.lastName,
                email = quoteRequestDto.email,
                currentInsurer = quoteRequestDto.currentInsurer,
                birthDate = quoteRequestDto.birthDate,
                ssn = quoteRequestDto.ssn,
                quotingPartner = quoteRequestDto.quotingPartner,
                incompleteQuoteData = when {
                    quoteRequestDto.incompleteApartmentQuoteData != null -> quoteRequestDto.incompleteApartmentQuoteData
                    quoteRequestDto.incompleteHouseQuoteData != null -> quoteRequestDto.incompleteHouseQuoteData
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
