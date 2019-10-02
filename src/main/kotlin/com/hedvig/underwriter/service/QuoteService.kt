package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.web.Dtos.*
import java.util.*
interface QuoteService {
    fun createCompleteQuote(incompleteQuoteId: UUID): CompleteQuoteResponseDto

    fun createIncompleteQuote(incompleteincompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto

    fun signQuote(completeQuoteId: UUID): SignedQuoteResponseDto

    fun getCompleteQuote(completeQuoteId: UUID): CompleteQuote
}