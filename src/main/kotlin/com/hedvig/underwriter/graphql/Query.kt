package com.hedvig.underwriter.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.hedvig.graphql.commons.extensions.getToken
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import graphql.schema.DataFetchingEnvironment
import java.util.UUID
import org.javamoney.moneta.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Query @Autowired constructor(
    private val quoteService: QuoteService,
    private val localizationService: LocalizationService,
    private val textKeysLocaleResolver: TextKeysLocaleResolver
) : GraphQLQueryResolver {

    // Do to discrepancy between the graphql schema and how the graphql library is implemented
    // we can but should never return QuoteResult.UnderwritingLimitsHit
    fun quote(id: UUID, env: DataFetchingEnvironment) = quoteService.getQuote(id)?.let { quote ->
        quote.toResult(env)
    } ?: throw IllegalStateException("No quote with id '$id' was found!")

    fun lastQuoteOfMember(env: DataFetchingEnvironment) =
        quoteService.getLatestQuoteForMemberId(env.getToken())?.let { quote ->
            quote.toResult(env)
        } ?: throw IllegalStateException("No quote found for memberId ${env.getToken()}!")

    private fun Quote.toResult(env: DataFetchingEnvironment) = when {
        isComplete -> {
            getCompleteQuoteResult(
                env,
                localizationService,
                textKeysLocaleResolver,
                quoteService.calculateInsuranceCost(this)
            )
        }
        else -> {
            getIncompleteQuoteResult(env, localizationService, textKeysLocaleResolver)
        }
    }
}
