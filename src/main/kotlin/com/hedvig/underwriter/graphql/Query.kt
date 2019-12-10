package com.hedvig.underwriter.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.service.QuoteService
import graphql.schema.DataFetchingEnvironment
import java.util.UUID
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
        when {
            quote.isComplete -> {
                quote.getCompleteQuoteResult(env, localizationService, textKeysLocaleResolver)
            }
            else -> {
                quote.getIncompleteQuoteResult(env, localizationService, textKeysLocaleResolver)
            }
        }
    } ?: throw IllegalStateException("No quote found!")
}
