package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.depricated.CompleteQuoteDetails
import com.hedvig.underwriter.localization.LocalizationService
import com.hedvig.underwriter.model.ExtraBuilding
import com.hedvig.underwriter.model.ExtraBuildingType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.birthDate
import com.hedvig.underwriter.model.email
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.model.norwegianHomeContents
import com.hedvig.underwriter.model.norwegianTravel
import com.hedvig.underwriter.model.ssnMaybe
import com.hedvig.underwriter.model.swedishApartment
import com.hedvig.underwriter.model.swedishHouse
import com.hedvig.underwriter.model.validTo
import com.hedvig.underwriter.util.toStockholmLocalDate
import java.time.LocalDate
import java.util.Locale
import org.springframework.stereotype.Component

@Component
class TypeMapper(
    private val localizationService: LocalizationService
) {
    fun mapToBundleQuote(
        quote: Quote,
        locale: Locale
    ): BundledQuote {
        return BundledQuote(
            id = quote.id,
            firstName = quote.firstName,
            lastName = quote.lastName,
            currentInsurer = quote.currentInsurer?.let { CurrentInsurer.create(it) },
            ssn = quote.ssnMaybe,
            birthDate = quote.birthDate,
            price = MonetaryAmountV2(
                quote.price!!.toPlainString(),
                quote.currency
            ),
            quoteDetails = mapToQuoteDetails(quote, locale),
            startDate = quote.startDate,
            expiresAt = quote.validTo.toStockholmLocalDate(),
            email = quote.email,
            dataCollectionId = quote.dataCollectionId
        )
    }

    fun mapToQuoteResult(
        quote: Quote,
        insuranceCost: InsuranceCost,
        locale: Locale
    ): QuoteResult {
        return when {
            quote.isComplete -> {
                mapToCompleteQuoteResult(
                    quote,
                    insuranceCost,
                    locale
                )
            }
            else -> {
                mapToIncompleteQuoteResult(
                    quote = quote,
                    locale = locale
                )
            }
        }
    }

    fun mapToIncompleteQuoteDetails(
        quote: Quote,
        locale: Locale
    ): IncompleteQuoteDetails? =
        quote.swedishApartment?.let { apartment ->
            IncompleteQuoteDetails.IncompleteApartmentQuoteDetails(
                street = apartment.street,
                zipCode = apartment.zipCode,
                householdSize = apartment.householdSize,
                livingSpace = apartment.livingSpace,
                type = apartment.subType?.let { ApartmentType.valueOf(it.name) }
            )
        } ?: quote.swedishHouse?.let { house ->
            IncompleteQuoteDetails.IncompleteHouseQuoteDetails(
                street = house.street,
                zipCode = house.zipCode,
                householdSize = house.householdSize,
                livingSpace = house.livingSpace,
                ancillarySpace = house.ancillaryArea,
                extraBuildings = house.extraBuildings?.map { extraBuildingInput ->
                    mapToExtraBuildingCore(extraBuildingInput, locale)
                },
                numberOfBathrooms = house.numberOfBathrooms,
                yearOfConstruction = house.yearOfConstruction,
                isSubleted = house.isSubleted
            )
        }

    fun mapToCompleteQuoteResult(
        quote: Quote,
        insuranceCost: InsuranceCost,
        locale: Locale
    ): QuoteResult.CompleteQuote {
        return QuoteResult.CompleteQuote(
            id = quote.id,
            firstName = quote.firstName,
            lastName = quote.lastName,
            email = quote.email,
            currentInsurer = quote.currentInsurer?.let { CurrentInsurer.create(it) },
            ssn = quote.ssnMaybe,
            birthDate = quote.birthDate,
            price = MonetaryAmountV2(
                quote.price!!.toPlainString(),
                "SEK"
            ),
            insuranceCost = insuranceCost,
            details = mapCompleteQuoteResult(
                quote,
                locale
            ),
            quoteDetails = mapToQuoteDetails(
                quote = quote,
                locale = locale
            ),
            expiresAt = quote.validTo.toStockholmLocalDate(),
            startDate = quote.startDate?.coerceAtLeast(LocalDate.now()),
            dataCollectionId = quote.dataCollectionId
        )
    }

    private fun mapToExtraBuildingCore(
        extraBuilding: ExtraBuilding,
        locale: Locale
    ): com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingCore {
        return when (extraBuilding.type) {
            ExtraBuildingType.GARAGE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGarage(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.CARPORT -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingCarport(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.SHED -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingShed(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.STOREHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingStorehouse(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.FRIGGEBOD -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingFriggebod(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.ATTEFALL -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingAttefall(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.OUTHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingOuthouse(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.GUESTHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGuesthouse(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.GAZEBO -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGazebo(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.GREENHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingGreenhouse(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.SAUNA -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingSauna(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.BARN -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingBarn(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.BOATHOUSE -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingBoathouse(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
            ExtraBuildingType.OTHER -> com.hedvig.underwriter.graphql.type.ExtraBuilding.ExtraBuildingOther(
                area = extraBuilding.area,
                hasWaterConnected = extraBuilding.hasWaterConnected,
                displayName = extractDisplayName(extraBuilding.type, locale)
            )
        }
    }

    private fun mapToIncompleteQuoteResult(
        quote: Quote,
        locale: Locale
    ) = QuoteResult.IncompleteQuote(
        id = quote.id,
        firstName = quote.firstName,
        lastName = quote.lastName,
        email = quote.email,
        birthDate = quote.birthDate,
        currentInsurer = quote.currentInsurer?.let { CurrentInsurer.create(it) },
        details = mapToIncompleteQuoteDetails(
            quote,
            locale
        ),
        startDate = quote.startDate?.coerceAtLeast(LocalDate.now()),
        dataCollectionId = quote.dataCollectionId
    )

    private fun mapToQuoteDetails(
        quote: Quote,
        locale: Locale
    ): QuoteDetails =
        quote.swedishApartment?.let { apartment ->
            QuoteDetails.SwedishApartmentQuoteDetails(
                street = apartment.street!!,
                zipCode = apartment.zipCode!!,
                householdSize = apartment.householdSize!!,
                livingSpace = apartment.livingSpace!!,
                type = ApartmentType.valueOf(apartment.subType!!.name)
            )
        } ?: quote.swedishHouse?.let { house ->
            QuoteDetails.SwedishHouseQuoteDetails(
                street = house.street!!,
                zipCode = house.zipCode!!,
                householdSize = house.householdSize!!,
                livingSpace = house.livingSpace!!,
                ancillarySpace = house.ancillaryArea!!,
                extraBuildings = house.extraBuildings!!.map { extraBuildingInput ->
                    mapToExtraBuildingCore(extraBuildingInput, locale)
                },
                numberOfBathrooms = house.numberOfBathrooms!!,
                yearOfConstruction = house.yearOfConstruction!!,
                isSubleted = house.isSubleted!!
            )
        } ?: quote.norwegianHomeContents?.let {
            QuoteDetails.NorwegianHomeContentsDetails(
                street = it.street,
                zipCode = it.zipCode,
                coInsured = it.coInsured,
                livingSpace = it.livingSpace,
                isYouth = it.isYouth,
                type = NorwegianHomeContentsType.valueOf(it.type.name)
            )
        } ?: quote.norwegianTravel?.let {
            QuoteDetails.NorwegianTravelDetails(
                coInsured = it.coInsured,
                isYouth = it.isYouth
            )
        }
        ?: throw IllegalStateException("Trying to create QuoteDetails without `swedishApartment`, `swedishHouse`, `norwegianHomeContents` or `norwegianTravel` data")

    private fun mapCompleteQuoteResult(
        quote: Quote,
        locale: Locale
    ): CompleteQuoteDetails =
        quote.swedishApartment?.let { apartment ->
            CompleteQuoteDetails.CompleteApartmentQuoteDetails(
                street = apartment.street!!,
                zipCode = apartment.zipCode!!,
                householdSize = apartment.householdSize!!,
                livingSpace = apartment.livingSpace!!,
                type = ApartmentType.valueOf(apartment.subType!!.name)
            )
        } ?: quote.swedishHouse?.let { house ->
            CompleteQuoteDetails.CompleteHouseQuoteDetails(
                street = house.street!!,
                zipCode = house.zipCode!!,
                householdSize = house.householdSize!!,
                livingSpace = house.livingSpace!!,
                ancillarySpace = house.ancillaryArea!!,
                extraBuildings = house.extraBuildings!!.map { extraBuildingInput ->
                    mapToExtraBuildingCore(extraBuildingInput, locale)
                },
                numberOfBathrooms = house.numberOfBathrooms!!,
                yearOfConstruction = house.yearOfConstruction!!,
                isSubleted = house.isSubleted!!
            )
        } ?: quote.norwegianHomeContents?.let {
            CompleteQuoteDetails.UnknownQuoteDetails()
        } ?: quote.norwegianTravel?.let {
            CompleteQuoteDetails.UnknownQuoteDetails()
        }
        ?: throw IllegalStateException("Trying to create QuoteDetails without `swedishApartment`, `swedishHouse`, `norwegianHomeContents` or `norwegianTravel` data")

    private fun extractDisplayName(ebt: ExtraBuildingType, locale: Locale): String =
        localizationService.getTranslation("EXTRA_BUILDING_DISPLAY_NAME_${ebt.name}", locale) ?: ebt.getDefaultDisplayName()
}
