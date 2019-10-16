package com.hedvig.underwriter.service

import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import com.hedvig.underwriter.model.DateWithZone
import com.hedvig.underwriter.model.HomeData
import com.hedvig.underwriter.model.PersonPolicyHolder
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorQuoteResponseDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteDto
import com.hedvig.underwriter.web.dtos.IncompleteQuoteResponseDto
import com.hedvig.underwriter.web.dtos.PostIncompleteQuoteRequest
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import java.time.Instant
import java.util.UUID
import javax.transaction.Transactional
import org.springframework.stereotype.Service

@Transactional
@Service
class QuoteServiceImpl(
    val debtChecker: DebtChecker,
    val memberService: MemberService,
    val productPricingService: ProductPricingService,
    val quoteRepository: QuoteRepository
) : QuoteService {

    override fun updateQuote(incompleteQuoteDto: IncompleteQuoteDto, id: UUID) {
        val quote = quoteRepository.load(id)
        quote!!.update(incompleteQuoteDto)
        quoteRepository.save(quote)
    }

    override fun createQuote(incompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto {
        val quote = Quote(
            UUID.randomUUID(),
            QuoteState.INCOMPLETE,
            Instant.now(),
            incompleteQuoteDto.productType,
            QuoteInitiatedFrom.PARTNER,
            HomeData()
        )

        quote.update(incompleteQuoteDto)
        quoteRepository.insert(quote)

        return IncompleteQuoteResponseDto(quote.id, quote.productType, quote.initiatedFrom)
    }

    override fun getQuote(completeQuoteId: UUID): Quote {
        return quoteRepository.load(completeQuoteId)
            ?: throw RuntimeException("No complete quote found with id $completeQuoteId")
    }

    override fun completeQuote(incompleteQuoteId: UUID): Either<ErrorQuoteResponseDto, CompleteQuoteResponseDto> {

        val quote = quoteRepository.load(incompleteQuoteId)

        val completeQuote = quote!!.complete(debtChecker, productPricingService)

        return when (completeQuote) {
            is Either.Left -> Left(ErrorQuoteResponseDto("quote cannot be calculated, underwriting guidelines are breached"))
            is Either.Right -> {
                quoteRepository.save(completeQuote.b)
                Right(CompleteQuoteResponseDto(completeQuote.b.id, completeQuote.b.price!!))
            }
        }
    }

    override fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): SignedQuoteResponseDto {
        try {
            val quote = getQuote(completeQuoteId)
            val updatedName = if (body.name != null && quote.data is PersonPolicyHolder<*>) {
                quote.copy(data = quote.data.updateName(firstName = body.name.firstName, lastName = body.name.lastName))
            } else { quote }

            val updatedStartTime = when {
                body.startDateWithZone != null -> {
                    val startDateWithZone: DateWithZone = body.startDateWithZone
                    updatedName.copy(startDate =
                            startDateWithZone.date.atStartOfDay().atZone(startDateWithZone.timeZone).toLocalDateTime())
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

            val signedQuote = updatedStartTime.copy(state = QuoteState.SIGNED)
            quoteRepository.save(signedQuote)

            return SignedQuoteResponseDto(signedQuoteId, Instant.now())
        } catch (exception: Exception) {
            throw RuntimeException("could not create a signed quote", exception)
        }
    }
}
