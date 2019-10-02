package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.web.Dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.SignedQuoteResponseDto
import java.util.*

interface QuoteService {
    fun createCompleteQuote(incompleteQuoteId: UUID): CompleteQuoteResponseDto

    fun signQuote(completeQuoteId: UUID): SignedQuoteResponseDto

    fun getCompleteQuote(completeQuoteId: UUID): CompleteQuote
}