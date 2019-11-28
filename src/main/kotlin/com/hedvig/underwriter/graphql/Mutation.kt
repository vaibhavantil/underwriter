package com.hedvig.underwriter.graphql

import arrow.core.Either
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.graphql.commons.extensions.getAcceptLanguage
import com.hedvig.graphql.commons.extensions.getTokenOrNull
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.extensions.createCompleteQuoteResult
import com.hedvig.underwriter.extensions.toIncompleteQuoteDto
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.CreateQuoteResult
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.RemoveCurrentInsurerInput
import com.hedvig.underwriter.graphql.type.UnderwritingLimit
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.web.dtos.ErrorCodes
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.context.GraphQLServletContext
import java.lang.IllegalStateException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Mutation @Autowired constructor(
    private val quoteService: QuoteService,
    private val localizationService: LocalizationService,
    private val textKeysLocaleResolver: TextKeysLocaleResolver
) : GraphQLMutationResolver {

    fun createQuote(input: CreateQuoteInput, env: DataFetchingEnvironment): CreateQuoteResult {
        val quote = quoteService.createQuote(
            input.toIncompleteQuoteDto(memberId = env.getTokenOrNull()),
            input.id,
            initiatedFrom = when {
                env.isAndroid() -> QuoteInitiatedFrom.ANDROID
                env.isIOS() -> QuoteInitiatedFrom.IOS
                else -> QuoteInitiatedFrom.WEBONBOARDING
            }
        )

        return when (val errorOrQuote = quoteService.completeQuote(quote.id)) {
            is Either.Left -> {
                when (errorOrQuote.a.errorCode) {
                    ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> {
                        CreateQuoteResult.UnderwritingLimitsHit(
                            errorOrQuote.a.breachedUnderwritingGuidelines?.map { breachedUnderwritingGuidelines ->
                                UnderwritingLimit(breachedUnderwritingGuidelines)
                            } ?: throw IllegalStateException("Breached underwriting guidelines with no list")
                        )
                    }
                    ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE ->
                        throw IllegalStateException("Member has existing insurance")
                    ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED ->
                        throw IllegalStateException("Quote has expired")
                    ErrorCodes.NO_SUCH_QUOTE ->
                        throw IllegalStateException("No such quote")
                    ErrorCodes.INVALID_STATE ->
                        throw IllegalStateException("Invalid state")
                    ErrorCodes.UNKNOWN_ERROR_CODE ->
                        throw IllegalStateException("Unknown error code")
                }
            }
            is Either.Right -> {
                val completeQuoteResponseDto = errorOrQuote.b

                CreateQuoteResult.CompleteQuote(
                    id = completeQuoteResponseDto.id,
                    firstName = input.firstName,
                    lastName = input.lastName,
                    currentInsurer = input.currentInsurer,
                    price = MonetaryAmountV2(
                        completeQuoteResponseDto.price.toPlainString(),
                        "SEK"
                    ),
                    details = input.createCompleteQuoteResult(
                        localizationService,
                        textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
                    ),
                    expiresAt = completeQuoteResponseDto.validTo
                )
            }
        }
    }

    fun editQuote(input: EditQuoteInput): CreateQuoteResult {
        TODO()
    }

    fun removeCurrentInsurer(input: RemoveCurrentInsurerInput): CreateQuoteResult {
        TODO()
    }

    fun DataFetchingEnvironment.isAndroid() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.getHeader("User-Agent")?.contains("Android", false)
            ?: false

    fun DataFetchingEnvironment.isIOS() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.getHeader("User-Agent")?.contains("iOS", false)
            ?: false
}
