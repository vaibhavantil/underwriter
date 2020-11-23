package com.hedvig.underwriter.serviceIntegration.notificationService

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsType
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.model.email
import com.hedvig.underwriter.model.firstName
import com.hedvig.underwriter.model.lastName
import com.hedvig.underwriter.model.ssnMaybe
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.guidelines.BaseGuideline
import com.hedvig.underwriter.service.guidelines.NorwegianHomeContentsGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianPersonGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianTravelGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishApartmentGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishHouseGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishPersonalGuidelines
import com.hedvig.underwriter.service.guidelines.TypedGuideline
import com.hedvig.underwriter.serviceIntegration.notificationService.dtos.QuoteCreatedEvent
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import org.javamoney.moneta.Money
import org.springframework.stereotype.Service

@Service
class StrategyService(
    private val debtChecker: DebtChecker,
    private val productPricingService: ProductPricingService
) {
    fun getStrategy(quote: Quote): QuoteStrategy = getStrategy(quote.data)

    fun getStrategy(quoteData: QuoteData): QuoteStrategy = when (quoteData) {
        is SwedishHouseData -> SwedishHouseDataStrategy(debtChecker, productPricingService)
        is SwedishApartmentData -> SwedishApartmentDataStrategy(debtChecker, productPricingService)
        is NorwegianHomeContentsData -> NorwegianHomeContentsDataStrategy(productPricingService)
        is NorwegianTravelData -> NorwegianTravelDataStrategy(productPricingService)
        is DanishHomeContentsData -> DanishHomeContentsDataStrategy()
        is DanishAccidentData -> DanishAccidentDataStrategy()
        is DanishTravelData -> DanishTravelDataStrategy()
    }
}

abstract class QuoteStrategy {

    abstract fun createNotificationEvent(quote: Quote): QuoteCreatedEvent
    abstract fun getInsuranceCost(quote: Quote): InsuranceCost
    abstract fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>>
    abstract fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>>

    inline fun <reified T : QuoteData> toTypedGuideline(it: BaseGuideline<T>) = TypedGuideline<QuoteData, T>(
        it,
        T::class
    )

    inline fun <reified T : QuoteData, reified Q : QuoteData> toTypedGuidelines(guidelines: Collection<BaseGuideline<Q>>) =
        guidelines.map { toTypedGuideline(it) }.toSet()
}

class SwedishHouseDataStrategy(
    private val debtChecker: DebtChecker,
    private val productPricingService: ProductPricingService
) : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is SwedishHouseData)
        return quoteCreatedEvent(quote, quote.data.street, quote.data.zipCode, "HOUSE")
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "SEK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishPersonalGuidelines(
            debtChecker
        ).setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishHouseGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class SwedishApartmentDataStrategy(
    private val debtChecker: DebtChecker,
    val productPricingService: ProductPricingService
) : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is SwedishApartmentData)
        return quoteCreatedEvent(quote, quote.data.street, quote.data.zipCode, quote.data.subType!!.toString())
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "SEK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishPersonalGuidelines(
            debtChecker
        ).setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return SwedishApartmentGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class NorwegianHomeContentsDataStrategy(val productPricingService: ProductPricingService) : QuoteStrategy() {

    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is NorwegianHomeContentsData)

        val insuranceType = if (quote.data.isYouth) {
            when (quote.data.type) {
                NorwegianHomeContentsType.RENT -> "YOUTH_RENT"
                NorwegianHomeContentsType.OWN -> "YOUTH_OWN"
            }
        } else {
            quote.data.type.name
        }

        return quoteCreatedEvent(quote, quote.data.street, quote.data.zipCode, insuranceType)
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "NOK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianPersonGuidelines.setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianHomeContentsGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class NorwegianTravelDataStrategy(val productPricingService: ProductPricingService) : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is NorwegianTravelData)

        return quoteCreatedEvent(
            quote, null, null,
            if (quote.data.isYouth) {
                "YOUTH"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return productPricingService.calculateInsuranceCost(
            Money.of(quote.price, "NOK"), quote.memberId!!
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianPersonGuidelines.setOfRules
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return NorwegianTravelGuidelines.setOfRules.map { toTypedGuideline(it) }.toSet()
    }
}

class DanishHomeContentsDataStrategy : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is DanishHomeContentsData)

        return quoteCreatedEvent(
            quote,
            quote.data.street,
            quote.data.zipCode,
            if (quote.data.isStudent) {
                "STUDENT"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return InsuranceCost(
            MonetaryAmountV2("9999.00", "DKK"),
            MonetaryAmountV2("0", "DKK"),
            MonetaryAmountV2("9999.00", "DKK"),
            null
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }
}

class DanishAccidentDataStrategy : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is DanishAccidentData)

        return quoteCreatedEvent(
            quote,
            quote.data.street,
            quote.data.zipCode,
            if (quote.data.isStudent) {
                "STUDENT"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return InsuranceCost(
            MonetaryAmountV2("9999.00", "DKK"),
            MonetaryAmountV2("0", "DKK"),
            MonetaryAmountV2("9999.00", "DKK"),
            null
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }
}

class DanishTravelDataStrategy : QuoteStrategy() {
    override fun createNotificationEvent(quote: Quote): QuoteCreatedEvent {
        require(quote.data is DanishAccidentData)

        return quoteCreatedEvent(
            quote,
            quote.data.street,
            quote.data.zipCode,
            if (quote.data.isStudent) {
                "STUDENT"
            } else {
                "REGULAR"
            }
        )
    }

    override fun getInsuranceCost(quote: Quote): InsuranceCost {
        return InsuranceCost(
            MonetaryAmountV2("9999.00", "DKK"),
            MonetaryAmountV2("0", "DKK"),
            MonetaryAmountV2("9999.00", "DKK"),
            null
        )
    }

    override fun getPersonalGuidelines(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }

    override fun getProductRules(data: QuoteData): Set<BaseGuideline<QuoteData>> {
        return setOf()
    }
}

@Service
class NotificationServiceImpl(
    private val client: NotificationServiceClient,
    private val strategyService: StrategyService
) : NotificationService {
    override fun sendQuoteCreatedEvent(quote: Quote) {
        val strategy = strategyService.getStrategy(quote)
        client.quoteCreated(
            strategy.createNotificationEvent(quote)
        )
    }
}

private fun quoteCreatedEvent(
    quote: Quote,
    street: String?,
    postalCode: String?,
    insuranceType: String
): QuoteCreatedEvent {
    return QuoteCreatedEvent(
        memberId = quote.memberId!!,
        quoteId = quote.id,
        firstName = quote.firstName,
        lastName = quote.lastName,
        street = street,
        postalCode = postalCode,
        email = quote.email!!,
        ssn = quote.ssnMaybe,
        initiatedFrom = quote.initiatedFrom.name,
        attributedTo = quote.attributedTo.name,
        productType = quote.productType.name,
        insuranceType = insuranceType,
        currentInsurer = quote.currentInsurer,
        price = quote.price,
        currency = quote.currency,
        originatingProductId = quote.originatingProductId
    )
}
