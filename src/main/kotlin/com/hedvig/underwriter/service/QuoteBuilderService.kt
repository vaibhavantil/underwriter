package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.PostIncompleteQuoteRequest
import java.util.*

interface QuoteBuilderService {
    fun findIncompleteQuoteById(id: UUID): Optional<IncompleteQuote>

    fun createIncompleteQuote(incompleteincompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto

    fun updateIncompleteQuoteData(incompleteincompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID)

    fun getIncompleteQuote(quoteId: UUID): IncompleteQuote

}

