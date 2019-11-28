package com.hedvig.underwriter.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import com.hedvig.underwriter.extensions.toCompleteQuote
import com.hedvig.underwriter.extensions.toIncompleteQuote
import com.hedvig.underwriter.graphql.type.Quote
import com.hedvig.underwriter.service.QuoteService
import java.util.UUID
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

// This is unfortunately needed
@Component
class Query @Autowired constructor(
    private val quoteService: QuoteService
) : GraphQLQueryResolver {

    fun quote(id: UUID): Quote {
        val quote = quoteService.getQuote(id)
            ?: throw RuntimeException("Quote not found!")

        return if (quote.price != null) {
            quote.toCompleteQuote()
        } else {
            quote.toIncompleteQuote()
        }
    }
}
