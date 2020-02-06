package com.hedvig.underwriter.model

import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData.SwedishApartment
import com.hedvig.underwriter.service.model.QuoteRequestData.SwedishHouse
import com.hedvig.underwriter.util.toStockholmLocalDate
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun String.birthDateFromSwedishSsn(): LocalDate {
    val trimmedInput = this.trim().replace("-", "").replace(" ", "")
    return LocalDate.parse(
        trimmedInput.substring(0, 4) + "-" + trimmedInput.substring(
            4,
            6
        ) + "-" + trimmedInput.substring(6, 8)
    )
}

fun String.birthDateFromNorwegianSsn(): LocalDate {
    TODO()
}

data class DatabaseQuoteRevision(
    val id: Int?,
    val masterQuoteId: UUID,
    val timestamp: Instant,
    val validity: Long,
    val productType: ProductType = ProductType.UNKNOWN,
    val state: QuoteState,
    val attributedTo: Partner,
    val currentInsurer: String? = "",
    val startDate: LocalDate? = null,
    val price: BigDecimal? = null,
    val quoteApartmentDataId: Int?,
    val quoteHouseDataId: Int?,
    val quoteNorwegianHomeContentsDataId: Int?,
    val quoteNorwegianTravelDataId: Int?,
    val memberId: String?,
    val breachedUnderwritingGuidelines: List<String>?,
    val underwritingGuidelinesBypassedBy: String?,
    val initiatedFrom: QuoteInitiatedFrom?,
    val createdAt: Instant?,
    val originatingProductId: UUID?,
    val signedProductId: UUID?,
    val dataCollectionId: UUID?
) {

    companion object {
        fun from(quote: Quote, id: Int? = null, timestamp: Instant = Instant.now()) =
            DatabaseQuoteRevision(
                id = id,
                masterQuoteId = quote.id,
                timestamp = timestamp,
                validity = quote.validity,
                productType = quote.productType,
                state = quote.state,
                attributedTo = quote.attributedTo,
                currentInsurer = quote.currentInsurer,
                startDate = quote.startDate,
                price = quote.price,
                quoteApartmentDataId = when (quote.data) {
                    is SwedishApartmentData -> quote.data.internalId
                    else -> null
                },
                quoteHouseDataId = when (quote.data) {
                    is SwedishHouseData -> quote.data.internalId
                    else -> null
                },
                quoteNorwegianHomeContentsDataId = when (quote.data) {
                    is NorwegianHomeContentsData -> quote.data.internalId
                    else -> null
                },
                quoteNorwegianTravelDataId = when (quote.data) {
                    is NorwegianTravelData -> quote.data.internalId
                    else -> null
                },
                memberId = quote.memberId,
                breachedUnderwritingGuidelines = quote.breachedUnderwritingGuidelines,
                underwritingGuidelinesBypassedBy = quote.underwritingGuidelinesBypassedBy,
                createdAt = quote.createdAt,
                initiatedFrom = quote.initiatedFrom,
                originatingProductId = quote.originatingProductId,
                signedProductId = quote.signedProductId,
                dataCollectionId = quote.dataCollectionId
            )
    }
}

const val ONE_DAY = 86_400L

data class Quote(
    val id: UUID,
    val createdAt: Instant,
    val price: BigDecimal? = null,
    val productType: ProductType = ProductType.UNKNOWN,
    val state: QuoteState,
    val initiatedFrom: QuoteInitiatedFrom,
    val attributedTo: Partner,
    val data: QuoteData,

    val currentInsurer: String? = null,

    val startDate: LocalDate? = null,

    val validity: Long = ONE_DAY * 30,
    val breachedUnderwritingGuidelines: List<String>?,
    val underwritingGuidelinesBypassedBy: String? = null,
    val memberId: String? = null,
    val originatingProductId: UUID? = null,
    val signedProductId: UUID? = null,
    val dataCollectionId: UUID? = null
) {
    val isComplete: Boolean
        get() = when {
            price == null -> false
            productType == ProductType.UNKNOWN -> false
            !data.isComplete -> false
            else -> true
        }

    fun update(quoteRequest: QuoteRequest): Quote {
        var newQuote = copy(
            productType = quoteRequest.productType ?: productType,
            startDate = quoteRequest.startDate?.toStockholmLocalDate() ?: startDate,
            data = when (data) {
                is SwedishApartmentData -> data.copy(
                    ssn = quoteRequest.ssn ?: data.ssn,
                    firstName = quoteRequest.firstName ?: data.firstName,
                    lastName = quoteRequest.lastName ?: data.lastName,
                    email = quoteRequest.email ?: data.email,
                    subType = when (val quoteData = quoteRequest.incompleteQuoteData) {
                        is SwedishApartment? -> quoteData?.subType ?: data.subType
                        else -> null
                    }
                ) as QuoteData // This cast removes an IntellJ warning
                is SwedishHouseData -> data.copy(
                    ssn = quoteRequest.ssn ?: data.ssn,
                    firstName = quoteRequest.firstName ?: data.firstName,
                    lastName = quoteRequest.lastName ?: data.lastName,
                    email = quoteRequest.email ?: data.email
                ) as QuoteData // This cast removes an IntellJ warning
                is NorwegianHomeContentsData -> TODO()
                is NorwegianTravelData -> TODO()
            }
        )

        val requestData = quoteRequest.incompleteQuoteData
        if (
            requestData is SwedishApartment
        ) {
            val newQuoteData: SwedishApartmentData = when (newQuote.data) {
                is SwedishApartmentData -> newQuote.data as SwedishApartmentData
                is SwedishHouseData -> {
                    val houseData = newQuote.data as SwedishHouseData
                    SwedishApartmentData(
                        id = houseData.id,
                        firstName = houseData.firstName,
                        lastName = houseData.lastName,
                        email = houseData.email,
                        ssn = houseData.ssn,
                        street = houseData.street,
                        zipCode = houseData.zipCode,
                        city = houseData.city,
                        householdSize = houseData.householdSize,
                        livingSpace = houseData.livingSpace
                    )
                }
                is NorwegianHomeContentsData -> TODO()
                is NorwegianTravelData -> TODO()
            }
            newQuote = newQuote.copy(
                data = newQuoteData.copy(
                    street = requestData.street ?: newQuoteData.street,
                    zipCode = requestData.zipCode ?: newQuoteData.zipCode,
                    city = requestData.city ?: newQuoteData.city,
                    householdSize = requestData.householdSize ?: newQuoteData.householdSize,
                    livingSpace = requestData.livingSpace ?: newQuoteData.livingSpace,
                    subType = requestData.subType ?: newQuoteData.subType
                )
            )
        }
        if (
            requestData is SwedishHouse
        ) {
            val newQuoteData: SwedishHouseData = when (newQuote.data) {
                is SwedishHouseData -> newQuote.data as SwedishHouseData
                is SwedishApartmentData -> {
                    val apartmentData = newQuote.data as SwedishApartmentData
                    SwedishHouseData(
                        id = apartmentData.id,
                        firstName = apartmentData.firstName,
                        lastName = apartmentData.lastName,
                        email = apartmentData.email,
                        ssn = apartmentData.ssn,
                        street = apartmentData.street,
                        zipCode = apartmentData.zipCode,
                        city = apartmentData.city,
                        householdSize = apartmentData.householdSize,
                        livingSpace = apartmentData.livingSpace
                    )
                }
                is NorwegianHomeContentsData -> TODO()
                is NorwegianTravelData -> TODO()
            }
            newQuote = newQuote.copy(
                data = newQuoteData.copy(
                    street = requestData.street ?: newQuoteData.street,
                    zipCode = requestData.zipCode ?: newQuoteData.zipCode,
                    city = requestData.city ?: newQuoteData.city,
                    householdSize = requestData.householdSize ?: newQuoteData.householdSize,
                    livingSpace = requestData.livingSpace ?: newQuoteData.livingSpace,
                    numberOfBathrooms = requestData.numberOfBathrooms ?: newQuoteData.numberOfBathrooms,
                    isSubleted = requestData.isSubleted ?: newQuoteData.isSubleted,
                    extraBuildings = requestData.extraBuildings?.map((ExtraBuilding)::from)
                        ?: newQuoteData.extraBuildings,
                    ancillaryArea = requestData.ancillaryArea ?: newQuoteData.ancillaryArea,
                    yearOfConstruction = requestData.yearOfConstruction ?: newQuoteData.yearOfConstruction
                )
            )
        }
        return newQuote
    }
}
