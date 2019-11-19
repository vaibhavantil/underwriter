package com.hedvig.underwriter.graphql

import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.underwriter.extensions.toIncompleteQuoteDto
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.graphql.type.RemoveCurrentInsurerInput
import com.hedvig.underwriter.service.QuoteService
import org.springframework.stereotype.Component

@Suppress("unused")
@Component
class Mutation(
    val quoteService: QuoteService
) : GraphQLMutationResolver {

    fun createQuote(input: CreateQuoteInput): QuoteResult {
        TODO()
    }

    fun editQuote(input: EditQuoteInput): QuoteResult {
        TODO()
    }

    fun removeCurrentInsurer(input: RemoveCurrentInsurerInput): QuoteResult {
        TODO()
    }
}

