package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.web.Dtos.*
import java.util.*

interface QuoteService {
    fun createCompleteQuote(incompleteQuoteId: UUID): Either<CompleteQuoteResponseDto, ErrorQuoteResponseDto>

    fun createIncompleteQuote(incompleteincompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto

    fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): SignedQuoteResponseDto

    fun getCompleteQuote(completeQuoteId: UUID): CompleteQuote
}