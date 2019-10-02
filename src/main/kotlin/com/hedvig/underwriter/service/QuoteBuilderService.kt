package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteResponseDto
import java.util.*

interface QuoteBuilderService {
    fun findIncompleteQuoteById(id: UUID): Optional<IncompleteQuote>

    fun createIncompleteQuote(incompleteincompleteQuoteDto: IncompleteQuoteDto): IncompleteQuoteResponseDto

    fun updateIncompleteQuoteData(incompleteincompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID)

    fun getIncompleteQuote(quoteId: UUID): IncompleteQuote

}

