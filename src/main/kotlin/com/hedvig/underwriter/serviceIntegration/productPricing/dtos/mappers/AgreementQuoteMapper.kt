package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers

import com.hedvig.productPricingObjects.dtos.AgreementQuote.DanishAccidentQuote
import com.hedvig.productPricingObjects.dtos.AgreementQuote.DanishHomeContentQuote
import com.hedvig.productPricingObjects.dtos.AgreementQuote.DanishTravelQuote
import com.hedvig.productPricingObjects.dtos.AgreementQuote.NorwegianHomeContentQuote
import com.hedvig.productPricingObjects.dtos.AgreementQuote.NorwegianTravelQuote
import com.hedvig.productPricingObjects.dtos.AgreementQuote.SwedishApartmentQuote
import com.hedvig.productPricingObjects.dtos.AgreementQuote.SwedishHouseQuote
import com.hedvig.productPricingObjects.dtos.CoInsured
import com.hedvig.productPricingObjects.enums.DanishAccidentLineOfBusiness
import com.hedvig.productPricingObjects.enums.DanishTravelLineOfBusiness
import com.hedvig.productPricingObjects.enums.NorwegianTravelLineOfBusiness
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import java.time.LocalDate

class AgreementQuoteMapper {
    companion object {
        fun toQuote(quote: Quote, fromDate: LocalDate? = null, toDate: LocalDate? = null) = when (quote.data) {
            is SwedishApartmentData -> SwedishApartmentQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                address = AddressMapper.toAddress(quote.data),
                coInsured = List(quote.data.householdSize!! - 1) { CoInsured(null, null, null) },
                squareMeters = quote.data.livingSpace!!.toLong(),
                lineOfBusiness = LineOfBusinessMapper.toLineOfBusiness(quote.data.subType!!)
            )
            is SwedishHouseData -> SwedishHouseQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                address = AddressMapper.toAddress(quote.data),
                coInsured = List(quote.data.householdSize!! - 1) { CoInsured(null, null, null) },
                squareMeters = quote.data.livingSpace!!.toLong(),
                ancillaryArea = quote.data.ancillaryArea!!.toLong(),
                yearOfConstruction = quote.data.yearOfConstruction!!,
                numberOfBathrooms = quote.data.numberOfBathrooms!!,
                extraBuildings = quote.data.extraBuildings!!.map { extraBuilding ->
                    ExtraBuildingMapper.toExtraBuildingDto(
                        extraBuilding
                    )
                },
                isSubleted = quote.data.isSubleted!!
            )
            is NorwegianHomeContentsData -> NorwegianHomeContentQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                address = AddressMapper.toAddress(quote.data),
                coInsured = List(quote.data.coInsured) { CoInsured(null, null, null) },
                squareMeters = quote.data.livingSpace.toLong(),
                lineOfBusiness = LineOfBusinessMapper.toLineOfBusiness(quote.data.type, quote.data.isYouth)
            )
            is NorwegianTravelData -> NorwegianTravelQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                coInsured = List(quote.data.coInsured) { CoInsured(null, null, null) },
                lineOfBusiness = if (quote.data.isYouth) NorwegianTravelLineOfBusiness.YOUTH else NorwegianTravelLineOfBusiness.REGULAR
            )
            is DanishHomeContentsData -> DanishHomeContentQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                address = AddressMapper.toAddress(quote.data),
                squareMeters = quote.data.livingSpace.toLong(),
                coInsured = List(quote.data.coInsured) { CoInsured(null, null, null) },
                lineOfBusiness = LineOfBusinessMapper.toLineOfBusiness(quote.data.type, quote.data.isStudent)
            )
            is DanishAccidentData -> DanishAccidentQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                address = AddressMapper.toAddress(quote.data),
                coInsured = List(quote.data.coInsured) { CoInsured(null, null, null) },
                lineOfBusiness = if (quote.data.isStudent) DanishAccidentLineOfBusiness.STUDENT else DanishAccidentLineOfBusiness.REGULAR
            )
            is DanishTravelData -> DanishTravelQuote(
                quoteId = quote.id,
                fromDate = fromDate ?: quote.startDate,
                toDate = toDate,
                premium = quote.price!!,
                currency = quote.currency,
                currentInsurer = quote.currentInsurer,
                address = AddressMapper.toAddress(quote.data),
                coInsured = List(quote.data.coInsured) { CoInsured(null, null, null) },
                lineOfBusiness = if (quote.data.isStudent) DanishTravelLineOfBusiness.STUDENT else DanishTravelLineOfBusiness.REGULAR
            )
        }
    }
}
