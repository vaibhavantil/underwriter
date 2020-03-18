package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = AgreementQuote.SwedishApartmentQuote::class, name = "SwedishApartment"),
    JsonSubTypes.Type(value = AgreementQuote.SwedishHouseQuote::class, name = "SwedishHouse"),
    JsonSubTypes.Type(value = AgreementQuote.NorwegianHomeContentQuote::class, name = "NorwegianHomeContent"),
    JsonSubTypes.Type(value = AgreementQuote.NorwegianTravelQuote::class, name = "NorwegianTravel")
)
sealed class AgreementQuote {
    abstract val quoteId: UUID
    abstract val fromDate: LocalDate?
    abstract val toDate: LocalDate?
    abstract val premium: BigDecimal
    abstract val currency: String

    data class SwedishApartmentQuote(
        override val quoteId: UUID,
        override val fromDate: LocalDate?,
        override val toDate: LocalDate?,
        override val premium: BigDecimal,
        override val currency: String,
        val address: AddressDto,
        val coInsured: List<CoInsuredDto>,
        val squareMeters: Long,
        val lineOfBusiness: SwedishApartmentLineOfBusiness
    ) : AgreementQuote()

    data class SwedishHouseQuote(
        override val quoteId: UUID,
        override val fromDate: LocalDate?,
        override val toDate: LocalDate?,
        override val premium: BigDecimal,
        override val currency: String,
        val address: AddressDto,
        val coInsured: List<CoInsuredDto>,
        val squareMeters: Long,
        val ancillaryArea: Long,
        val yearOfConstruction: Int,
        val numberOfBathrooms: Int,
        val extraBuildings: List<ExtraBuildingDto>,
        val isSubleted: Boolean
    ) : AgreementQuote()

    data class NorwegianHomeContentQuote(
        override val quoteId: UUID,
        override val fromDate: LocalDate?,
        override val toDate: LocalDate?,
        override val premium: BigDecimal,
        override val currency: String,
        val address: AddressDto,
        val coInsured: List<CoInsuredDto>,
        val squareMeters: Long,
        val lineOfBusiness: NorwegianHomeContentLineOfBusiness
    ) : AgreementQuote()

    data class NorwegianTravelQuote(
        override val quoteId: UUID,
        override val fromDate: LocalDate?,
        override val toDate: LocalDate?,
        override val premium: BigDecimal,
        override val currency: String,
        val coInsured: List<CoInsuredDto>,
        val lineOfBusiness: NorwegianTravelLineOfBusiness
    ) : AgreementQuote()

    companion object {
        fun from(quote: Quote, fromDate: LocalDate? = null, toDate: LocalDate? = null) = when (quote.data) {
            is SwedishApartmentData -> SwedishApartmentQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                address = AddressDto.from(quote.data),
                coInsured = List(quote.data.householdSize!! - 1) { CoInsuredDto(null, null, null) },
                squareMeters = quote.data.livingSpace!!.toLong(),
                lineOfBusiness = SwedishApartmentLineOfBusiness.from(quote.data.subType!!)
            )
            is SwedishHouseData -> SwedishHouseQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                address = AddressDto.from(quote.data),
                coInsured = List(quote.data.householdSize!! - 1) { CoInsuredDto(null, null, null) },
                squareMeters = quote.data.livingSpace!!.toLong(),
                ancillaryArea = quote.data.ancillaryArea!!.toLong(),
                yearOfConstruction = quote.data.yearOfConstruction!!,
                numberOfBathrooms = quote.data.numberOfBathrooms!!,
                extraBuildings = quote.data.extraBuildings!!.map((ExtraBuildingDto)::from),
                isSubleted = quote.data.isSubleted!!
            )
            is NorwegianHomeContentsData -> NorwegianHomeContentQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                address = AddressDto.from(quote.data),
                coInsured = List(quote.data.coInsured) { CoInsuredDto(null, null, null) },
                squareMeters = quote.data.livingSpace.toLong(),
                lineOfBusiness = NorwegianHomeContentLineOfBusiness.from(quote.data.type, quote.data.isYouth)
            )
            is NorwegianTravelData -> NorwegianTravelQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                coInsured = List(quote.data.coInsured) { CoInsuredDto(null, null, null) },
                lineOfBusiness = if (quote.data.isYouth) NorwegianTravelLineOfBusiness.YOUTH else NorwegianTravelLineOfBusiness.REGULAR
            )
        }
    }
}
