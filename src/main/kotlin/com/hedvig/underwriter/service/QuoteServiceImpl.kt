package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.DateWithZone
import com.hedvig.underwriter.model.PersonPolicyHolder
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.service.exceptions.QuoteNotFoundException
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorQuoteResponseDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import java.time.Instant
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class QuoteServiceImpl(
    val debtChecker: DebtChecker,
    val memberService: MemberService,
    val productPricingService: ProductPricingService,
    val quoteRepository: QuoteRepository
) : QuoteService {

    override fun updateQuote(incompleteQuoteDto: IncompleteQuoteDto, id: UUID) {
        val quote = quoteRepository.find(id) ?: throw QuoteNotFoundException("No such quote $id")
        quote.update(incompleteQuoteDto)
        quoteRepository.update(quote)
    }

    override fun createQuote(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuoteResponseDto {
        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            productType = incompleteQuoteDto.productType,
            initiatedFrom = QuoteInitiatedFrom.PARTNER,
            data = ApartmentData(UUID.randomUUID())
        )

        quoteRepository.insert(quote.update(incompleteQuoteDto))

        return IncompleteQuoteResponseDto(quote.id, quote.productType, quote.initiatedFrom)
    }

    override fun getQuote(completeQuoteId: UUID): Quote? {
        return quoteRepository.find(completeQuoteId)
    }

    override fun completeQuote(incompleteQuoteId: UUID): Either<ErrorQuoteResponseDto, CompleteQuoteResponseDto> {
        val quote =
            quoteRepository.find(incompleteQuoteId) ?: return Either.left(ErrorQuoteResponseDto("No such quote"))

        return quote.complete(debtChecker, productPricingService)
            .bimap(
                { ErrorQuoteResponseDto("quote cannot be calculated, underwriting guidelines are breached") },
                { completeQuote ->
                    quoteRepository.update(completeQuote)
                    CompleteQuoteResponseDto(id = completeQuote.id, price = completeQuote.price!!)
                }
            )
    }

    override fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): SignedQuoteResponseDto {
        try {
            val quote = getQuote(completeQuoteId)
                ?: throw QuoteNotFoundException("Quote $completeQuoteId not found when trying to sign")
            val updatedName = if (body.name != null && quote.data is PersonPolicyHolder<*>) {
                quote.copy(data = quote.data.updateName(firstName = body.name.firstName, lastName = body.name.lastName))
            } else {
                quote
            }

            val updatedStartTime = when {
                body.startDateWithZone != null -> {
                    val startDateWithZone: DateWithZone = body.startDateWithZone
                    updatedName.copy(
                        startDate =
                        startDateWithZone.date
                    )
                }
                else -> updatedName.copy(startDate = null)
            }

            val memberId = memberService.createMember()

            if (updatedStartTime.data is PersonPolicyHolder<*>) {
                memberService.updateMemberSsn(memberId.toLong(), UpdateSsnRequest(ssn = updatedStartTime.data.ssn!!))
            }

            val signedQuoteId =
                productPricingService.createProduct(updatedStartTime.getRapioQuoteRequestDto(body.email), memberId).id

            if (updatedStartTime.data is PersonPolicyHolder<*>) {
                memberService.signQuote(memberId.toLong(), UnderwriterQuoteSignRequest(updatedStartTime.data.ssn!!))
            }

            val signedQuote = updatedStartTime.copy(signedAt = Instant.now())
            quoteRepository.update(signedQuote)

            return SignedQuoteResponseDto(signedQuoteId, signedQuote.signedAt!!)
        } catch (exception: Exception) {
            throw RuntimeException("could not create a signed quote", exception)
        }
    }
}
