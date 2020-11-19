package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.guidelines.BreachedGuideline
import com.hedvig.underwriter.service.model.QuoteRequest
import java.util.UUID

interface Underwriter {
    fun createQuote(
        quoteRequest: QuoteRequest,
        id: UUID,
        initiatedFrom: QuoteInitiatedFrom,
        underwritingGuidelinesBypassedBy: String?
    ): Either<Pair<Quote, List<BreachedGuideline>>, Quote>

    fun updateQuote(
        quote: Quote,
        underwritingGuidelinesBypassedBy: String?
    ): Either<Pair<Quote, List<BreachedGuideline>>, Quote>
}
