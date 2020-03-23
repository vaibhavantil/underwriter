package com.hedvig.underwriter.serviceIntegration.priceEngine.dtos

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.NorwegianHomeContentLineOfBusiness
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.NorwegianTravelLineOfBusiness
import java.time.LocalDate
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = PriceQueryRequest.NorwegianHomeContent::class, name = "NorwegianHomeContent"),
    JsonSubTypes.Type(value = PriceQueryRequest.NorwegianTravel::class, name = "NorwegianTravel")
)
sealed class PriceQueryRequest {
    abstract val holderMemberId: String?
    abstract val quoteId: UUID?
    abstract val holderBirthDate: LocalDate
    abstract val numberCoInsured: Int

    data class NorwegianHomeContent(
        override val holderMemberId: String?,
        override val quoteId: UUID?,
        override val holderBirthDate: LocalDate,
        override val numberCoInsured: Int,
        val lineOfBusiness: NorwegianHomeContentLineOfBusiness,
        val postalCode: String,
        val squareMeters: Int
    ) : PriceQueryRequest() {
        companion object {
            fun from(quoteId: UUID, memberId: String?, data: NorwegianHomeContentsData) = NorwegianHomeContent(
                holderMemberId = memberId,
                quoteId = quoteId,
                holderBirthDate = data.birthDate,
                numberCoInsured = data.coInsured,
                lineOfBusiness = NorwegianHomeContentLineOfBusiness.from(data.type, data.isYouth),
                postalCode = data.zipCode,
                squareMeters = data.livingSpace
            )
        }
    }

    data class NorwegianTravel(
        override val holderMemberId: String?,
        override val quoteId: UUID?,
        override val holderBirthDate: LocalDate,
        override val numberCoInsured: Int,
        val lineOfBusiness: NorwegianTravelLineOfBusiness
    ) : PriceQueryRequest() {
        companion object {
            fun from(quoteId: UUID, memberId: String?, data: NorwegianTravelData) = NorwegianTravel(
                holderMemberId = memberId,
                quoteId = quoteId,
                holderBirthDate = data.birthDate,
                numberCoInsured = data.coInsured,
                lineOfBusiness = NorwegianTravelLineOfBusiness.from(data.isYouth)
            )
        }
    }
}
