package com.hedvig.underwriter.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.hedvig.graphql.commons.extensions.getAcceptLanguage
import com.hedvig.graphql.commons.extensions.getToken
import com.hedvig.resolver.LocaleResolver
import com.hedvig.underwriter.graphql.type.QuoteBundle
import com.hedvig.underwriter.graphql.type.QuoteBundleInputInput
import com.hedvig.underwriter.graphql.type.QuoteMapper
import com.hedvig.underwriter.graphql.type.SignMethod
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.service.BundleQuotesService
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.SignService
import com.hedvig.underwriter.util.logger
import com.hedvig.underwriter.util.toMaskedString
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class Query @Autowired constructor(
    private val quoteService: QuoteService,
    private val signService: SignService,
    private val bundleQuotesService: BundleQuotesService,
    private val quoteMapper: QuoteMapper
) : GraphQLQueryResolver {

    fun quote(id: UUID, env: DataFetchingEnvironment) {

        logger.info("Get quote for quoteId=$id")

        quoteService.getQuote(id)?.let { quote ->
            quote.toResult(env)
        } ?: throw QuoteNotFoundQueryException("No quote with id '$id' was found!")
    }

    fun lastQuoteOfMember(env: DataFetchingEnvironment) {

        logger.info("Get last quote for memberId=${env.getToken()}")

        quoteService.getLatestQuoteForMemberId(env.getToken())?.toResult(env)
            ?: throw QuoteNotFoundQueryException("No quote found for memberId: ${env.getToken()}")
    }

    fun quoteBundle(input: QuoteBundleInputInput, env: DataFetchingEnvironment): QuoteBundle {

        logger.info("Get quote bundle: memberId=${env.getToken()}, request: ${input.toMaskedString()}")

        if (input.ids.isEmpty()) {
            throw EmptyBundleQueryException()
        }

        return bundleQuotesService.bundleQuotes(
            env.getToken(),
            input.ids,
            LocaleResolver.resolveLocale(env.getAcceptLanguage())
        )
    }

    fun signMethodForQuotes(input: List<UUID>): SignMethod {

        logger.info("Get sign method for quotes: quoteIds=$input")

        return signService.getSignMethodFromQuotes(input).toGraphQL()
    }

    private fun Quote.toResult(env: DataFetchingEnvironment) = quoteMapper.mapToQuoteResult(
        this,
        quoteService.calculateInsuranceCost(this),
        LocaleResolver.resolveLocale(env.getAcceptLanguage())
    )
}
