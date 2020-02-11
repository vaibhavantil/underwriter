package com.hedvig.underwriter.model

import com.hedvig.service.LocalizationService
import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.graphql.type.IncompleteQuoteDetails
import com.hedvig.underwriter.graphql.type.NorwegianHomeContentsType
import com.hedvig.underwriter.graphql.type.QuoteDetails
import com.hedvig.underwriter.graphql.type.depricated.CompleteQuoteDetails
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData.SwedishApartment
import com.hedvig.underwriter.service.model.QuoteRequestData.SwedishHouse
import com.hedvig.underwriter.util.toStockholmLocalDate
import java.lang.IllegalStateException
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.Locale
import java.util.UUID

val Quote.firstName
    get() = (data as? PersonPolicyHolder<*>)?.firstName
        ?: throw RuntimeException("No firstName on Quote! $this")

val Quote.lastName
    get() = (data as? PersonPolicyHolder<*>)?.lastName
        ?: throw RuntimeException("No lastName on Quote! $this")

val Quote.ssn
    get() = (data as? PersonPolicyHolder<*>)?.ssn
        ?: throw RuntimeException("No ssn on Quote! $this")

val Quote.email
    get() = (data as? PersonPolicyHolder<*>)?.email

val Quote.swedishApartment
    get() = (data as? SwedishApartmentData)

val Quote.swedishHouse
    get() = (data as? SwedishHouseData)

val Quote.norwegianHomeContents
    get() = (data as? NorwegianHomeContentsData)

val Quote.norwegianTravel
    get() = (data as? NorwegianTravelData)

val Quote.validTo
    get() = this.createdAt.plusSeconds(this.validity)!!

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
    return LocalDate.parse(this.birthDateStringFromNorwegianSsn())
}

fun String.birthDateStringFromNorwegianSsn(): String {
    val trimmedInput = this.trim().replace("-", "").replace(" ", "")
    val day = trimmedInput.substring(0, 2)
    val month = trimmedInput.substring(2, 4)
    val twoDigitYear = trimmedInput.substring(4, 6).toInt()
    val breakPoint = LocalDate.now().minusYears(10).year.toString().substring(2, 4).toInt()

    val year = if (twoDigitYear > breakPoint) {
        "19$twoDigitYear"
    } else {
        "20$twoDigitYear"
    }
    return "$year-$month-$day"
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

    val currency: String
        get() = when (this.data) {
            is SwedishApartmentData -> SEK
            is SwedishHouseData -> SEK
            is NorwegianTravelData -> NOK
            is NorwegianHomeContentsData -> NOK
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

    fun createIncompleteQuoteResult(
        localizationService: LocalizationService,
        locale: Locale
    ): IncompleteQuoteDetails? =
        this.swedishApartment?.let { apartment ->
            IncompleteQuoteDetails.IncompleteApartmentQuoteDetails(
                street = apartment.street,
                zipCode = apartment.zipCode,
                householdSize = apartment.householdSize,
                livingSpace = apartment.livingSpace,
                type = apartment.subType?.let { ApartmentType.valueOf(it.name) }
            )
        } ?: this.swedishHouse?.let { house ->
            IncompleteQuoteDetails.IncompleteHouseQuoteDetails(
                street = house.street,
                zipCode = house.zipCode,
                householdSize = house.householdSize,
                livingSpace = house.livingSpace,
                ancillarySpace = house.ancillaryArea,
                extraBuildings = house.extraBuildings?.map { extraBuildingInput ->
                    extraBuildingInput.toGraphQLResponseObject(localizationService, locale)
                },
                numberOfBathrooms = house.numberOfBathrooms,
                yearOfConstruction = house.yearOfConstruction,
                isSubleted = house.isSubleted
            )
        }

    fun createQuoteDetails(
        localizationService: LocalizationService,
        locale: Locale
    ): QuoteDetails =
        this.swedishApartment?.let { apartment ->
            QuoteDetails.SwedishApartmentQuoteDetails(
                street = apartment.street!!,
                zipCode = apartment.zipCode!!,
                householdSize = apartment.householdSize!!,
                livingSpace = apartment.livingSpace!!,
                type = ApartmentType.valueOf(apartment.subType!!.name)
            )
        } ?: this.swedishHouse?.let { house ->
            QuoteDetails.SwedishHouseQuoteDetails(
                street = house.street!!,
                zipCode = house.zipCode!!,
                householdSize = house.householdSize!!,
                livingSpace = house.livingSpace!!,
                ancillarySpace = house.ancillaryArea!!,
                extraBuildings = house.extraBuildings!!.map { extraBuildingInput ->
                    extraBuildingInput.toGraphQLResponseObject(localizationService, locale)
                },
                numberOfBathrooms = house.numberOfBathrooms!!,
                yearOfConstruction = house.yearOfConstruction!!,
                isSubleted = house.isSubleted!!
            )
        } ?: this.norwegianHomeContents?.let {
            QuoteDetails.NorwegianHomeContentsDetails(
                street = it.street,
                zipCode = it.zipCode,
                coInsured = it.coInsured,
                livingSpace = it.livingSpace,
                isStudent = it.isStudent,
                type = NorwegianHomeContentsType.valueOf(it.type.name)
            )
        } ?: this.norwegianTravel?.let {
            QuoteDetails.NorwegianTravelDetails(
                coInsured = it.coInsured
            )
        }
        ?: throw IllegalStateException("Trying to create QuoteDetails without `swedishApartment`, `swedishHouse`, `norwegianHomeContents` or `norwegianTravel` data")

    fun createCompleteQuoteResult(
        localizationService: LocalizationService,
        locale: Locale
    ): CompleteQuoteDetails =
        this.swedishApartment?.let { apartment ->
            CompleteQuoteDetails.CompleteApartmentQuoteDetails(
                street = apartment.street!!,
                zipCode = apartment.zipCode!!,
                householdSize = apartment.householdSize!!,
                livingSpace = apartment.livingSpace!!,
                type = ApartmentType.valueOf(apartment.subType!!.name)
            )
        } ?: this.swedishHouse?.let { house ->
            CompleteQuoteDetails.CompleteHouseQuoteDetails(
                street = house.street!!,
                zipCode = house.zipCode!!,
                householdSize = house.householdSize!!,
                livingSpace = house.livingSpace!!,
                ancillarySpace = house.ancillaryArea!!,
                extraBuildings = house.extraBuildings!!.map { extraBuildingInput ->
                    extraBuildingInput.toGraphQLResponseObject(localizationService, locale)
                },
                numberOfBathrooms = house.numberOfBathrooms!!,
                yearOfConstruction = house.yearOfConstruction!!,
                isSubleted = house.isSubleted!!
            )
        } ?: this.norwegianHomeContents?.let {
            CompleteQuoteDetails.UnknownQuoteDetails()
        } ?: this.norwegianTravel?.let {
            CompleteQuoteDetails.UnknownQuoteDetails()
        } ?: throw IllegalStateException("Trying to create QuoteDetails without `swedishApartment`, `swedishHouse` data")

    companion object {
        private const val SEK = "SEK"
        private const val NOK = "NOK"
    }
}
