package com.hedvig.underwriter.graphql

import arrow.core.Either
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.underwriter.extensions.toIncompleteQuoteDto
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.graphql.type.RemoveCurrentInsurerInput
import com.hedvig.underwriter.graphql.type.UnderwritingLimit
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.web.dtos.ErrorCodes
import org.springframework.stereotype.Component
import java.lang.IllegalStateException

@Suppress("unused")
@Component
class Mutation(
    val quoteService: QuoteService
) : GraphQLMutationResolver {

    fun createQuote(input: CreateQuoteInput): QuoteResult {
        val quote = quoteService.createQuote(input.toIncompleteQuoteDto())

        return when (val quoteOrError = quoteService.completeQuote(quote.id)) {
            is Either.Left -> {
                when (quoteOrError.a.errorCode) {
                    ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> {
                        QuoteResult.UnderwritingLimitsHit(
                            quoteOrError.a.breachedUnderwritingGuidelines?.map { breachedUnderwritingGuidelines ->
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
                TODO()
            }
        }
    }

    fun editQuote(input: EditQuoteInput): QuoteResult {
        TODO()
    }

    fun removeCurrentInsurer(input: RemoveCurrentInsurerInput): QuoteResult {
        TODO()
    }
}

