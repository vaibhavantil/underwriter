package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.web.Dtos.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
interface QuoteService {
    fun createCompleteQuote(incompleteQuoteId: UUID): CompleteQuoteResponseDto

    fun createIncompleteQuote(incompleteincompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto

    fun signQuote(completeQuoteId: UUID, request: ChooseActiveFromDto): SignedQuoteResponseDto

    fun getCompleteQuote(completeQuoteId: UUID): CompleteQuote
}