package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.web.Dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteResponseDto
import java.util.*

interface QuoteService {
    fun findIncompleteQuoteById(id: UUID): Optional<IncompleteQuote>

    fun createIncompleteQuote(incompleteincompleteQuoteDto: IncompleteQuoteDto): IncompleteQuoteResponseDto

    fun updateIncompleteQuoteData(incompleteincompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID)

    fun createCompleteQuote(incompleteQuoteId: UUID):CompleteQuoteResponseDto
}

