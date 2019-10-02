package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.Dtos.CompleteQuoteResponseDto
import com.hedvig.underwriter.web.Dtos.SignedQuoteResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class QuoteServiceImpl @Autowired constructor(
        val quoteBuilderService: QuoteBuilderService,
        val debtChecker: DebtChecker,
        val uwGuidelinesChecker: UwGuidelinesChecker,
        val productPricingService: ProductPricingService,
        val completeQuoteRepository: CompleteQuoteRepository,
        val memberService: MemberService
): QuoteService {
    override fun getCompleteQuote(completeQuoteId: UUID): CompleteQuote {
        val optionalQuote: Optional<CompleteQuote> = completeQuoteRepository.findById(completeQuoteId)
        if (!optionalQuote.isPresent) throw RuntimeException("No complete quote found with id $completeQuoteId")
        return optionalQuote.get()
    }

    override fun createCompleteQuote(incompleteQuoteId: UUID): CompleteQuoteResponseDto {
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
            return SignedQuoteResponseDto(signedQuoteId, Instant.now())
        } catch(exception: Exception) {
            throw RuntimeException("could not create a signed quote", exception)
        }
    }


}