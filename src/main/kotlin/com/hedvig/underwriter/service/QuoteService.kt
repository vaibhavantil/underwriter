package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorQuoteResponseDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.PostIncompleteQuoteRequest
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import java.util.UUID

interface QuoteService {
    fun completeQuote(incompleteQuoteId: UUID): Either<ErrorQuoteResponseDto, CompleteQuoteResponseDto>

    fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): SignedQuoteResponseDto

    fun getQuote(completeQuoteId: UUID): Quote
    fun createQuote(incompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto
    fun updateQuote(incompleteQuoteDto: IncompleteQuoteDto, id: UUID)
}
