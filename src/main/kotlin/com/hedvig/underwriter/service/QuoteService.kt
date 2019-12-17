package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.dtos.HouseOrApartmentIncompleteQuoteDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuoteDto
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import java.time.LocalDate
import java.util.UUID

interface QuoteService {
    fun completeQuote(
        incompleteQuoteId: UUID,
        underwritingGuidelinesBypassedBy: String? = null
    ): Either<ErrorResponseDto, CompleteQuoteResponseDto>
    fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): Either<ErrorResponseDto, SignedQuoteResponseDto>
    fun memberSigned(memberId: String, signRequest: SignRequest)
    fun activateQuote(
        completeQuoteId: UUID,
        activationDate: LocalDate? = null,
        previousProductTerminationDate: LocalDate? = null
    ): Either<ErrorResponseDto, Quote>

    fun getQuote(completeQuoteId: UUID): Quote?
    fun getSingleQuoteForMemberId(memberId: String): QuoteDto?
    fun getLatestQuoteForMemberId(memberId: String): Quote?
    fun getQuotesForMemberId(memberId: String): List<QuoteDto>
    fun createQuote(
        houseOrApartmentIncompleteQuoteDto: HouseOrApartmentIncompleteQuoteDto,
        id: UUID? = null,
        initiatedFrom: QuoteInitiatedFrom = QuoteInitiatedFrom.RAPIO
    ): IncompleteQuoteResponseDto
    fun updateQuote(
        houseOrApartmentIncompleteQuoteDto: HouseOrApartmentIncompleteQuoteDto,
        id: UUID,
        underwritingGuidelinesBypassedBy: String? = null
    ): Either<ErrorResponseDto, Quote>
    fun removeCurrentInsurerFromQuote(
        id: UUID
    ): Either<ErrorResponseDto, Quote>
}
