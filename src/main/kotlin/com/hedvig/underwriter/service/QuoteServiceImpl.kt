package com.hedvig.underwriter.service

import arrow.core.Either
import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.model.DateWithZone
import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.repository.IncompleteQuoteRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.Dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*
import javax.transaction.Transactional

@Transactional
@Service
class QuoteServiceImpl @Autowired constructor(
        val incompleteQuoteRepository: IncompleteQuoteRepository,
        val completeQuoteRepository: CompleteQuoteRepository,
        val quoteBuilderService: QuoteBuilderService,
        val debtChecker: DebtChecker,
        val uwGuidelinesChecker: UwGuidelinesChecker,
        val memberService: MemberService,
        val productPricingService: ProductPricingService
) : QuoteService {

    override fun createIncompleteQuote(incompleteincompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto {
        val incompleteQuote = incompleteQuoteRepository.save(IncompleteQuote.from(incompleteincompleteQuoteDto))
        return IncompleteQuoteResponseDto(incompleteQuote.id!!, incompleteQuote.productType, incompleteQuote.quoteInitiatedFrom)
    }


    override fun getCompleteQuote(completeQuoteId: UUID): CompleteQuote {
        val optionalQuote: Optional<CompleteQuote> = completeQuoteRepository.findById(completeQuoteId)
        if (!optionalQuote.isPresent) throw RuntimeException("No complete quote found with id $completeQuoteId")
        return optionalQuote.get()

    }

    override fun createCompleteQuote(incompleteQuoteId: UUID): Either<ErrorQuoteResponseDto, CompleteQuoteResponseDto> {

        val incompleteQuote = quoteBuilderService.getIncompleteQuote(incompleteQuoteId)
        val completeQuote = incompleteQuote.complete()

        val debtCheckPassed = completeQuote.passedDebtCheck(debtChecker)
        val uwGuidelinesPassed = completeQuote.passedUnderwritingGuidelines(uwGuidelinesChecker)

        if(!completeQuote.memberIsOver30()) completeQuote.reasonQuoteCannotBeCompleted += "member is younger than 18"

        if(completeQuote.ssnIsValid() && debtCheckPassed && uwGuidelinesPassed && completeQuote.memberIsOlderThan18()) {
            completeQuote.setPriceRetrievedFromProductPricing(productPricingService)
            completeQuoteRepository.save(completeQuote)
            return Either.right(CompleteQuoteResponseDto(completeQuote.id.toString(), completeQuote.price!!))
        }
        completeQuoteRepository.save(completeQuote)

        incompleteQuote.quoteState = QuoteState.QUOTED
        incompleteQuoteRepository.save(incompleteQuote)

        return Either.left(ErrorQuoteResponseDto("quote cannot be calculated, underwriting guidelines are breached"))
    }

    override fun signQuote(completeQuoteId: UUID, body: SignQuoteRequest): Any {
        try {
            val completeQuote = getCompleteQuote(completeQuoteId)
            if (body.name != null) {
                completeQuote.firstName = body.name.firstName
                completeQuote.lastName = body.name.lastName
            }

            when {
                body.startDateWithZone != null -> {
                    val startDateWithZone: DateWithZone = body.startDateWithZone
                    completeQuote.startDate =
                            startDateWithZone.date.atStartOfDay().atZone(startDateWithZone.timeZone).toLocalDateTime()
                }
                else -> completeQuote.startDate = null
            }

            val memberId = memberService.createMember()

            memberService.updateMemberSsn(memberId!!.toLong(), UpdateSsnRequest(ssn = completeQuote.ssn))

            val signedQuoteId =
                    productPricingService.createProduct(completeQuote.getRapioQuoteRequestDto(body.email), memberId).id

            val memberServiceSignedQuote =
                    memberService.signQuote(memberId.toLong(), UnderwriterQuoteSignRequest(completeQuote.ssn))

            return when(memberServiceSignedQuote) {
                is Either.Left -> {
                    memberServiceSignedQuote.a
                }
                is Either.Right -> {

                    completeQuote.quoteState = QuoteState.SIGNED
                    completeQuoteRepository.save(completeQuote)

                    return SignedQuoteResponseDto(signedQuoteId, Instant.now())
                }
            }
        } catch(exception: Exception) {
            throw RuntimeException("could not create a signed quote", exception)
        }
    }
}