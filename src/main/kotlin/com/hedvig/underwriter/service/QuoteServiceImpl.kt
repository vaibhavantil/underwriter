package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.model.IncompleteQuote
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.repository.IncompleteQuoteRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterSignQuoteRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.Dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

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

    override fun createCompleteQuote(incompleteQuoteId: UUID): CompleteQuoteResponseDto {
//        change state of imcomplete quote and save to repo

        val incompleteQuote = quoteBuilderService.getIncompleteQuote(incompleteQuoteId)
        val completeQuote = incompleteQuote.complete()

        val debtCheckPassed = completeQuote.passedDebtCheck(debtChecker)
        val uwGuidelinesPassed = completeQuote.passedUnderwritingGuidelines(uwGuidelinesChecker)

        if(completeQuote.ssnIsValid() && debtCheckPassed && uwGuidelinesPassed) {
            completeQuote.setPriceRetrievedFromProductPricing(productPricingService)
            completeQuoteRepository.save(completeQuote)
            return CompleteQuoteResponseDto(completeQuote.id, completeQuote.price)
        }
        completeQuoteRepository.save(completeQuote)
        throw RuntimeException("${completeQuote.reasonQuoteCannotBeCompleted}")
    }

    override fun signQuote(completeQuoteId: UUID): SignedQuoteResponseDto {
//        TODO: complete
        try {
            val completeQuote = getCompleteQuote(completeQuoteId)
            val memberId = memberService.createMember()
            val signedQuoteId = productPricingService.createProduct(completeQuote.getRapioQuoteRequestDto(), memberId!!).id
//            go to memberservice and pass ssn
//            change state of completeQuote and save to repo

            val quoteRequest = UnderwriterSignQuoteRequest(
                    ssn = completeQuote.ssn
            )

            memberService.signQuote(quoteRequest, memberId)
            return SignedQuoteResponseDto(signedQuoteId, Instant.now())
        } catch(exception: Exception) {
            throw RuntimeException("could not create a signed quote", exception)
        }
    }


}