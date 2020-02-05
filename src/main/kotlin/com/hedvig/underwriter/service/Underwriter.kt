package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.ExtraBuilding
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.guidelines.AgeRestrictionGuideline
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.PersonalDebt
import com.hedvig.underwriter.service.guidelines.SocialSecurityDate
import com.hedvig.underwriter.service.guidelines.SocialSecurityNumberFormat
import com.hedvig.underwriter.service.guidelines.SwedishApartmentHouseHoldSizeAtLeast1
import com.hedvig.underwriter.service.guidelines.SwedishApartmentHouseHoldSizeNotMoreThan6
import com.hedvig.underwriter.service.guidelines.SwedishApartmentLivingSpaceAtLeast1Sqm
import com.hedvig.underwriter.service.guidelines.SwedishApartmentLivingSpaceNotMoreThan250Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseExtraBuildingsSizeAtLeast1Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseExtraBuildingsSizeNotOverThan75Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseHouseholdSizeAtLeast1
import com.hedvig.underwriter.service.guidelines.SwedishHouseHouseholdSizeNotMoreThan6
import com.hedvig.underwriter.service.guidelines.SwedishHouseLivingSpaceAtLeast1Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseLivingSpaceNotMoreThan250Sqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseNumberOfBathrooms
import com.hedvig.underwriter.service.guidelines.SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm
import com.hedvig.underwriter.service.guidelines.SwedishHouseYearOfConstruction
import com.hedvig.underwriter.service.guidelines.SwedishStudentApartmentAgeNotMoreThan30Years
import com.hedvig.underwriter.service.guidelines.SwedishStudentApartmentHouseholdSizeNotMoreThan2
import com.hedvig.underwriter.service.guidelines.SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ApartmentQuotePriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HouseQuotePriceDto
import com.hedvig.underwriter.util.toStockholmLocalDate
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class UnderwriterImpl(
    val debtChecker: DebtChecker,
    val productPricingService: ProductPricingService
) : Underwriter {

    override fun createQuote(
        quoteRequest: QuoteRequest,
        id: UUID,
        initiatedFrom: QuoteInitiatedFrom,
        underwritingGuidelinesBypassedBy: String?
    ): Either<List<String>, Quote> {
        val now = Instant.now()

        val quote = Quote(
            id = id,
            createdAt = now,
            productType = quoteRequest.productType!!,
            initiatedFrom = initiatedFrom,
            attributedTo = quoteRequest.quotingPartner ?: Partner.HEDVIG,
            data = when (val quoteData = quoteRequest.incompleteQuoteData) {
                is QuoteRequestData.Apartment ->
                    SwedishApartmentData(
                        id = UUID.randomUUID(),
                        ssn = quoteRequest.ssn,
                        firstName = quoteRequest.firstName,
                        lastName = quoteRequest.lastName,
                        email = quoteRequest.email,
                        subType = quoteData.subType,
                        street = quoteData.street,
                        zipCode = quoteData.zipCode,
                        city = quoteData.city,
                        householdSize = quoteData.householdSize,
                        livingSpace = quoteData.livingSpace
                    )
                is QuoteRequestData.House ->
                    SwedishHouseData(
                        id = UUID.randomUUID(),
                        ssn = quoteRequest.ssn,
                        firstName = quoteRequest.firstName,
                        lastName = quoteRequest.lastName,
                        email = quoteRequest.email,
                        street = quoteData.street,
                        zipCode = quoteData.zipCode,
                        city = quoteData.city,
                        householdSize = quoteData.householdSize,
                        livingSpace = quoteData.livingSpace,
                        numberOfBathrooms = quoteData.numberOfBathrooms,
                        isSubleted = quoteData.isSubleted,
                        extraBuildings = quoteData.extraBuildings?.map((ExtraBuilding)::from),
                        ancillaryArea = quoteData.ancillaryArea,
                        yearOfConstruction = quoteData.yearOfConstruction
                    )
                null -> throw IllegalArgumentException("Must provide either house or apartment data")
            },
            state = QuoteState.INCOMPLETE,
            memberId = quoteRequest.memberId,
            breachedUnderwritingGuidelines = null,
            originatingProductId = quoteRequest.originatingProductId,
            currentInsurer = quoteRequest.currentInsurer,
            startDate = quoteRequest.startDate?.toStockholmLocalDate(),
            dataCollectionId = quoteRequest.dataCollectionId,
            underwritingGuidelinesBypassedBy = underwritingGuidelinesBypassedBy
        )

        return validateAndCompleteQuote(quote, underwritingGuidelinesBypassedBy)
    }

    override fun updateQuote(quote: Quote, underwritingGuidelinesBypassedBy: String?): Either<List<String>, Quote> {
        return validateAndCompleteQuote(quote, underwritingGuidelinesBypassedBy)
    }

    private fun validateAndCompleteQuote(quote: Quote, underwritingGuidelinesBypassedBy: String?): Either<List<String>, Quote> {
        val breachedUnderwritingGuidelines = mutableListOf<String>()
        if (underwritingGuidelinesBypassedBy == null) {
            breachedUnderwritingGuidelines.addAll(
                validateGuidelines(quote.data)
            )
        }

        return if (breachedUnderwritingGuidelines.isEmpty())
            Either.right(complete(quote))
        else
            Either.left(breachedUnderwritingGuidelines)
    }

    private fun complete(quote: Quote): Quote {
        val price = getPriceRetrievedFromProductPricing(quote)
        return quote.copy(
            price = price,
            state = QuoteState.QUOTED
        )
    }

    private fun getPriceRetrievedFromProductPricing(quote: Quote): BigDecimal {
        return when (quote.data) {
            is SwedishApartmentData -> productPricingService.priceFromProductPricingForApartmentQuote(
                ApartmentQuotePriceDto.from(quote)
            ).price
            is SwedishHouseData -> productPricingService.priceFromProductPricingForHouseQuote(
                HouseQuotePriceDto.from(quote)
            ).price
            // TODO: This needs to be fixed should be done by the underwriter
            is NorwegianHomeContentsData -> BigDecimal.ZERO
            is NorwegianTravelData -> BigDecimal.ZERO
        }
    }

    // TODO: Change me
    val swedishApartmentGuidelines = listOf(
        SwedishApartmentHouseHoldSizeAtLeast1(),
        SwedishApartmentLivingSpaceAtLeast1Sqm(),
        SwedishApartmentHouseHoldSizeNotMoreThan6(),
        SwedishApartmentLivingSpaceNotMoreThan250Sqm()
    )

    val swedishStudentApartmentGuidelines = listOf(
        SwedishApartmentHouseHoldSizeAtLeast1(),
        SwedishApartmentLivingSpaceAtLeast1Sqm(),
        SwedishStudentApartmentHouseholdSizeNotMoreThan2(),
        SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm(),
        SwedishStudentApartmentAgeNotMoreThan30Years()
    )

    val swedishHouseGuideline = listOf(
        SwedishHouseHouseholdSizeAtLeast1(),
        SwedishHouseLivingSpaceAtLeast1Sqm(),
        SwedishHouseHouseholdSizeNotMoreThan6(),
        SwedishHouseLivingSpaceNotMoreThan250Sqm(),
        SwedishHouseYearOfConstruction(),
        SwedishHouseNumberOfBathrooms(),
        SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm(),
        SwedishHouseExtraBuildingsSizeNotOverThan75Sqm(),
        SwedishHouseExtraBuildingsSizeAtLeast1Sqm()
    )

    val swedishPersonalGuidelines = listOf(
        SocialSecurityNumberFormat,
        SocialSecurityDate,
        AgeRestrictionGuideline,
        PersonalDebt(debtChecker)
    )

    fun validateGuidelines(data: QuoteData): List<String> {
        val errors = mutableListOf<String>()

        errors.addAll(runRules(data, swedishPersonalGuidelines))

        when (data) {
            is SwedishHouseData -> errors.addAll(runRules(data, swedishHouseGuideline))
            is SwedishApartmentData -> when (data.subType) {
                ApartmentProductSubType.STUDENT_BRF, ApartmentProductSubType.STUDENT_RENT ->
                    errors.addAll(runRules(data, swedishStudentApartmentGuidelines))
                else -> errors.addAll(runRules(data, swedishApartmentGuidelines))
            }
            is NorwegianHomeContentsData -> TODO("todo")
            is NorwegianTravelData -> TODO("todo")
        }
        return errors
    }

    fun <T> runRules(
        data: T,
        listOfRules: List<BaseGuideline<T>>
    ): MutableList<String> {
        val guidelineErrors = mutableListOf<String>()

        listOfRules.forEach {
            val error = it.invokeValidate(data)
            if (error != null) {
                guidelineErrors.add(error)
            }
            if (it.skipAfter) {
                return@forEach
            }
        }
        return guidelineErrors
    }
}

interface Underwriter {
    fun createQuote(
        quoteRequest: QuoteRequest,
        id: UUID,
        initiatedFrom: QuoteInitiatedFrom,
        underwritingGuidelinesBypassedBy: String?
    ): Either<List<String>, Quote>

    fun updateQuote(
        quote: Quote,
        underwritingGuidelinesBypassedBy: String?
    ): Either<List<String>, Quote>
}
