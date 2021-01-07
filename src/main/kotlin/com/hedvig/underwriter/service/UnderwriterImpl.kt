package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.BreachedGuidelineCode
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.quoteStrategies.QuoteStrategyService
import com.hedvig.underwriter.serviceIntegration.priceEngine.PriceEngineService
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryRequest
import com.hedvig.underwriter.util.toStockholmLocalDate
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class UnderwriterImpl(
    private val priceEngineService: PriceEngineService,
    private val quoteStrategyService: QuoteStrategyService,
    private val metrics: Metrics
) : Underwriter {

    override fun createQuote(
        quoteRequest: QuoteRequest,
        id: UUID,
        initiatedFrom: QuoteInitiatedFrom,
        underwritingGuidelinesBypassedBy: String?
    ): Either<Pair<Quote, List<BreachedGuidelineCode>>, Quote> {
        val now = Instant.now()

        val quote = Quote(
            id = id,
            createdAt = now,
            productType = quoteRequest.productType!!,
            initiatedFrom = initiatedFrom,
            attributedTo = quoteRequest.quotingPartner
                ?: Partner.HEDVIG,
            data = createQuoteData(quoteRequest),
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

    private fun createQuoteData(quoteRequest: QuoteRequest): QuoteData =
        quoteRequest.incompleteQuoteData?.createQuoteData(quoteRequest)
            ?: throw IllegalArgumentException("Must provide either house or apartment data")

    override fun validateAndCompleteQuote(
        quote: Quote,
        underwritingGuidelinesBypassedBy: String?
    ): Either<Pair<Quote, List<BreachedGuidelineCode>>, Quote> {
        val breachedUnderwritingGuidelines = mutableListOf<BreachedGuidelineCode>()
        if (underwritingGuidelinesBypassedBy == null) {
            breachedUnderwritingGuidelines.addAll(
                validateGuidelines(quote)
            )
        }
        return if (breachedUnderwritingGuidelines.isEmpty()) {
            Either.right(complete(quote))
        } else {
            Either.left(
                quote.copy(
                    breachedUnderwritingGuidelines = breachedUnderwritingGuidelines.map { it }
                ) to breachedUnderwritingGuidelines
            )
        }
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
            is SwedishApartmentData -> priceEngineService.querySwedishApartmentPrice(
                PriceQueryRequest.SwedishApartment.from(quote.id, quote.memberId, quote.data, quote.dataCollectionId)
            ).priceBigDecimal
            is SwedishHouseData -> priceEngineService.querySwedishHousePrice(
                PriceQueryRequest.SwedishHouse.from(quote.id, quote.memberId, quote.data, quote.dataCollectionId)
            ).priceBigDecimal
            is NorwegianHomeContentsData -> priceEngineService.queryNorwegianHomeContentPrice(
                PriceQueryRequest.NorwegianHomeContent.from(quote.id, quote.memberId, quote.data)
            ).priceBigDecimal
            is NorwegianTravelData -> priceEngineService.queryNorwegianTravelPrice(
                PriceQueryRequest.NorwegianTravel.from(quote.id, quote.memberId, quote.data)
            ).priceBigDecimal
            is DanishHomeContentsData -> {
                // TODO: fix when pricing is in place
                BigDecimal(9999)
            }
            is DanishAccidentData -> {
                // TODO: fix when pricing is in place
                BigDecimal(6666)
            }
            is DanishTravelData -> {
                // TODO: fix when pricing is in place
                BigDecimal(3333)
            }
        }
    }

    fun validateGuidelines(data: Quote): List<BreachedGuidelineCode> {
        val errors = mutableListOf<BreachedGuidelineCode>()

        val guidelines = quoteStrategyService.getAllGuidelines(data)
        errors.addAll(runRules(data.data, guidelines))
        errors.forEach {
            metrics.increment(data.market, it)
        }
        return errors
    }

    fun <T : QuoteData> runRules(
        data: T,
        listOfRules: Set<BaseGuideline<T>>
    ): MutableList<BreachedGuidelineCode> {
        val guidelineErrors = mutableListOf<BreachedGuidelineCode>()

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
