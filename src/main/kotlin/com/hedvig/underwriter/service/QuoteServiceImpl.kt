package com.hedvig.underwriter.service

import arrow.core.Either
import arrow.core.Right
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.PersonPolicyHolder
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.service.exceptions.QuoteNotFoundException
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
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

    override fun updateQuote(incompleteQuoteDto: IncompleteQuoteDto, id: UUID): Quote {
        val quote = quoteRepository
            .find(id)
            ?.update(incompleteQuoteDto)
            ?: throw QuoteNotFoundException("No such quote $id")
        return quoteRepository.update(quote)
    }

    override fun createApartmentQuote(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuoteResponseDto {
        val quote = Quote(
            id = UUID.randomUUID(),
            createdAt = Instant.now(),
            productType = ProductType.APARTMENT,
            initiatedFrom = QuoteInitiatedFrom.PARTNER,
            data = ApartmentData(UUID.randomUUID())
        )

        quoteRepository.insert(quote.update(incompleteQuoteDto))

        return IncompleteQuoteResponseDto(quote.id, quote.productType, quote.initiatedFrom)
    }

    override fun getQuote(completeQuoteId: UUID): Quote? {
        return quoteRepository.find(completeQuoteId)
    }

    override fun completeQuote(incompleteQuoteId: UUID): Either<ErrorResponseDto, CompleteQuoteResponseDto> {
        val quote =
            quoteRepository.find(incompleteQuoteId)
                ?: return Either.left(ErrorResponseDto(ErrorCodes.UNKNOWN_ERROR_CODE, "No such quote"))

        return quote.complete(debtChecker, productPricingService)
            .bimap(
                {
                    ErrorResponseDto(
                        ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES,
                        "quote cannot be calculated, underwriting guidelines are breached"
                    )
                },
                { completeQuote ->
                    quoteRepository.update(completeQuote)
                    CompleteQuoteResponseDto(id = completeQuote.id, price = completeQuote.price!!)
                }
            )
    }

    override fun signQuote(
        completeQuoteId: UUID,
        body: SignQuoteRequest
    ): Either<ErrorResponseDto, SignedQuoteResponseDto> {
        try {
            val quote = getQuote(completeQuoteId)
                ?: throw QuoteNotFoundException("Quote $completeQuoteId not found when trying to sign")

            if (quote.state == QuoteState.EXPIRED) {
                return Either.Left(
                    ErrorResponseDto(
                        ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED,
                        "cannot sign quote it has expired"
                    )
                )
            }

            val updatedName = if (body.name != null && quote.data is PersonPolicyHolder<*>) {
                quote.copy(data = quote.data.updateName(firstName = body.name.firstName, lastName = body.name.lastName))
            } else {
                quote
            }

            val updatedStartTime = when {
                body.startDate != null -> {
                    updatedName.copy(
                        startDate = body.startDate
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

            return Right(SignedQuoteResponseDto(signedQuoteId, signedQuote.signedAt!!))
        } catch (exception: Exception) {
            throw RuntimeException("could not create a signed quote", exception)
        }
    }
}
