package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.web.dtos.*
import java.util.*

interface QuoteService {
    fun completeQuote(incompleteQuoteId: UUID): Either<ErrorQuoteResponseDto, CompleteQuoteResponseDto>

    fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): SignedQuoteResponseDto

    fun getQuote(completeQuoteId: UUID): Quote
    fun createQuote(incompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto
    fun updateQuote(incompleteQuoteDto: IncompleteQuoteDto, id: UUID)
}