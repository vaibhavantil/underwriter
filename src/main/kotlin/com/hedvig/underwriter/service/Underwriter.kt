package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.ExtraBuilding
import com.hedvig.underwriter.model.HouseData
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.birthDateFromSsn
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.util.toStockholmLocalDate
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class UnderwriterImpl(
    val debtChecker: DebtChecker,
    val productPricingService: ProductPricingService
) : Underwriter {

    fun createQuote(
        quoteRequest: QuoteRequest,
        id: UUID?,
        initiatedFrom: QuoteInitiatedFrom,
        underwritingGuidelinesBypassedBy: String?
    ): Either<ErrorResponseDto, CompleteQuoteResponseDto> {
        val now = Instant.now()

        val quote = Quote(
            id = id ?: UUID.randomUUID(),
            createdAt = now,
            productType = quoteRequest.productType!!,
            initiatedFrom = initiatedFrom,
            attributedTo = quoteRequest.quotingPartner ?: Partner.HEDVIG,
            data = when (val quoteData = quoteRequest.incompleteQuoteData) {
                is QuoteRequestData.Apartment ->
                    ApartmentData(
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
                    HouseData(
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
            dataCollectionId = quoteRequest.dataCollectionId
        )

        val breachedUnderwritingGuidelines = mutableListOf<String>()

        breachedUnderwritingGuidelines.addAll(
            validateGuidelines(quote.data)
        )

        if (breachedUnderwritingGuidelines.isEmpty())

        complete(quote, underwritingGuidelinesBypassedBy)

        return transformCompleteQuoteReturn(potentiallySavedQuote, quote.id)
    }

    private fun complete(
        quote: Quote,
        underwritingGuidelinesBypassedBy: String? = null
    ): Either<List<String>, Quote> {

        if (breachedUnderwritingGuidelines.isNotEmpty() && bypassUnderwritingGuidelines) {
            return Either.right(
                newQuote.copy(
                    price = getPriceRetrievedFromProductPricing(productPricingService),
                    underwritingGuidelinesBypassedBy = underwritingGuidelinesBypassedBy!!,
                    state = QuoteState.QUOTED
                )
            )
        }

        if (breachedUnderwritingGuidelines.isEmpty()) {
            return Either.right(
                newQuote.copy(
                    price = getPriceRetrievedFromProductPricing(productPricingService),
                    state = QuoteState.QUOTED
                )
            )
        }

        return Either.left(breachedUnderwritingGuidelines)
    }

    private fun validateGuidelines(data: QuoteData): List<String> {
        val errors = mutableListOf<String>()

        return when (data) {
            is HouseData -> validateGuidelines(data, errors)
            is ApartmentData -> validateGuidelines(data, errors)
        }
    }

    private fun validateGuidelines(data: HouseData, errors: MutableList<String>): List<String> {
        errors.addAll(swedishGuidelines(data, errors))

        if (data.householdSize!! < 1) {
            errors += "breaches underwriting guideline household size, must be at least 1"
        }
        if (data.livingSpace!! < 1) {
            errors += "breaches underwriting guidline living space, must be at least 1 sqm"
        }

        if (data.householdSize!! > 6) {
            errors += "breaches underwriting guideline household size, must not be more than 6"
        }

        if (data.livingSpace!! > 250) {
            errors += "breaches underwriting guideline living space, must not be more than 250 sqm"
        }

        if (data.yearOfConstruction!! < 1925) {
            errors += "breaches underwriting guideline year of construction, must not be older than 1925"
        }

        if (data.numberOfBathrooms!! > 2) {
            errors += "breaches underwriting guideline number of bathrooms, must not be more than 2"
        }

        if (data.extraBuildings!!.filter { building -> building.area > 6 }.size > 4) {
            errors += "breaches underwriting guideline extra building areas, number of extra buildings with an area over 6 sqm must not be more than 4"
        }

        if (data.extraBuildings.any { building -> building.area > 75 }) {
            errors += "breaches underwriting guideline extra building areas, extra buildings may not be over 75 sqm"
        }

        if (data.extraBuildings.any { building -> building.area < 1 }) {
            errors += "breaches underwriting guideline extra building areas, extra buildings must have an area of at least 1"
        }

        return errors
    }

    private fun validateGuidelines(data: ApartmentData, errors: MutableList<String>): List<String> {
        errors.addAll(swedishGuidelines(data, errors))

        if (data.householdSize!! < 1) {
            errors.add("breaches underwriting guideline household size, must be at least 1")
        }
        if (data.livingSpace!! < 1) {
            errors.add("breaches underwriting guidline living space, must be at least 1")
        }

        when (data.subType) {
            ApartmentProductSubType.STUDENT_RENT, ApartmentProductSubType.STUDENT_BRF -> {
                if (data.householdSize > 2) errors.add("breaches underwriting guideline household size must be less than 2")
                if (data.livingSpace > 50) errors.add("breaches underwriting guideline living space must be less than or equal to 50sqm")
                if (data.ssn!!.birthDateFromSsn().until(
                        LocalDate.now(),
                        ChronoUnit.YEARS
                    ) > 30
                ) errors.add("breaches underwriting guidelines member must be 30 years old or younger")
            }
            else -> {
                if (data.householdSize > 6) errors.add("breaches underwriting guideline household size must be less than or equal to 6")
                if (data.livingSpace > 250) errors.add("breaches underwriting guideline living space must be less than or equal to 250sqm")
            }
        }

        return errors
    }

    private fun swedishGuidelines(
        personalPolicyHolder: PersonPolicyHolder<*>,
        errors: MutableList<String>
    ): List<String> {
        if (personalPolicyHolder.age() < 18) {
            errors.add("member is younger than 18")
            return errors
        }

        val trimmedInput = personalPolicyHolder.ssn!!.trim().replace("-", "").replace(" ", "")

        if (trimmedInput.length != 12) {
            errors.add("SSN Invalid length")
            return errors
        }

        try {
            LocalDate.parse(
                trimmedInput.substring(0, 4) + "-" + trimmedInput.substring(
                    4,
                    6
                ) + "-" + trimmedInput.substring(6, 8)
            )
        } catch (exception: Exception) {
            errors.add("ssn not valid")
            return errors
        }

        if (errors.isEmpty()) {
            errors.addAll(debtChecker.passesDebtCheck(personalPolicyHolder))
        }
        return errors
    }
}

interface Underwriter
