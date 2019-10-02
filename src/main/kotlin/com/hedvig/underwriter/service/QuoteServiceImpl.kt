package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.*
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.repository.IncompleteQuoteRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuotePriceResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.Dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.NullPointerException
import java.util.*

@Service
class QuoteServiceImpl @Autowired constructor(
        val incompleteQuoteRepository: IncompleteQuoteRepository,
        val completeQuoteRepository: CompleteQuoteRepository,
        val productPricingService: ProductPricingService,
        val uwGuidelinesChecker: UwGuidelinesChecker,
        val memberService: MemberService,
        val debtChecker: DebtChecker
) : QuoteService {
    override fun createIncompleteQuote(incompleteincompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto {
        val incompleteQuote = incompleteQuoteRepository.save(IncompleteQuote.from(incompleteincompleteQuoteDto))
        return IncompleteQuoteResponseDto(incompleteQuote.id!!, incompleteQuote.productType, incompleteQuote.quoteInitiatedFrom)
    }

    //    TODO: refactor
    override fun updateIncompleteQuoteData(incompleteincompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID) {

        val incompleteQuote = getIncompleteQuote(quoteId)

        if (incompleteincompleteQuoteDto.lineOfBusiness != null) incompleteQuote.lineOfBusiness = incompleteincompleteQuoteDto.lineOfBusiness
        if (incompleteincompleteQuoteDto.quoteInitiatedFrom != null) incompleteQuote.quoteInitiatedFrom = incompleteincompleteQuoteDto.quoteInitiatedFrom
        if (incompleteincompleteQuoteDto.birthDate != null) incompleteQuote.birthDate = incompleteincompleteQuoteDto.birthDate
        if (incompleteincompleteQuoteDto.livingSpace != null) incompleteQuote.livingSpace = incompleteincompleteQuoteDto.livingSpace
        if (incompleteincompleteQuoteDto.houseHoldSize != null) incompleteQuote.houseHoldSize = incompleteincompleteQuoteDto.houseHoldSize
        if (incompleteincompleteQuoteDto.isStudent != null) incompleteQuote.isStudent = incompleteincompleteQuoteDto.isStudent
        if (incompleteincompleteQuoteDto.ssn != null) incompleteQuote.ssn = incompleteincompleteQuoteDto.ssn

        if (incompleteincompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is House) {
            val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto? = incompleteincompleteQuoteDto.incompleteQuoteDataDto.incompleteHouseQuoteDataDto
            val incompleteHouseQuoteData: House = incompleteQuote.incompleteQuoteData as House

            if (incompleteHouseQuoteDataDto?.zipcode != null) incompleteHouseQuoteData.zipcode = incompleteHouseQuoteDataDto.zipcode
            if (incompleteHouseQuoteDataDto?.city != null) incompleteHouseQuoteData.city = incompleteHouseQuoteDataDto.city
            if (incompleteHouseQuoteDataDto?.street != null) incompleteHouseQuoteData.street = incompleteHouseQuoteDataDto.street
            if (incompleteHouseQuoteDataDto?.householdSize != null) incompleteHouseQuoteData.householdSize = incompleteHouseQuoteDataDto.householdSize
            if (incompleteHouseQuoteDataDto?.livingSpace != null) incompleteHouseQuoteData.livingSpace = incompleteHouseQuoteDataDto.livingSpace
        }

        if (incompleteincompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is Home) {
            val incommingData: IncompleteHomeQuoteDataDto? = incompleteincompleteQuoteDto.incompleteQuoteDataDto.incompleteHomeQuoteDataDto
            var incompleteHomeQuoteData: Home = incompleteQuote.incompleteQuoteData as Home

            //if (incompleteHomeQuoteDataDto?.numberOfRooms != null) incompleteHomeQuoteData.numberOfRooms = incompleteHomeQuoteDataDto.numberOfRooms
            if (incommingData?.address != null) incompleteHomeQuoteData = incompleteHomeQuoteData.copy(address = incommingData.address)
            if (incommingData?.zipCode != null) incompleteHomeQuoteData = incompleteHomeQuoteData.copy(zipCode = incommingData.zipCode)
            if (incommingData?.floor != null) incompleteHomeQuoteData = incompleteHomeQuoteData.copy(floor = incommingData.floor)
            incompleteQuote.incompleteQuoteData = incompleteHomeQuoteData
        }

        incompleteQuoteRepository.save(incompleteQuote)
    }

    override fun findIncompleteQuoteById(id: UUID): Optional<IncompleteQuote> {
        return incompleteQuoteRepository.findById(id)
    }

    override fun createCompleteQuote(incompleteQuoteId: UUID): QuotePriceResponseDto  {
        val incompleteQuote = getIncompleteQuote(incompleteQuoteId)
        val completeQuote = incompleteQuote.complete()

        val debtCheckPassed = completeQuote.passedDebtCheck(debtChecker)
        val uwGuidelinesPassed = completeQuote.passedUnderwritingGuidelines(uwGuidelinesChecker)

        if(debtCheckPassed && uwGuidelinesPassed) {
            completeQuote.setPriceRetrievedFromProductPricing(productPricingService)
            completeQuoteRepository.save(completeQuote)
            return QuotePriceResponseDto(completeQuote.price)
        }
        completeQuoteRepository.save(completeQuote)
        throw RuntimeException("${completeQuote.reasonQuoteCannotBeCompleted}")
    }

    private fun getIncompleteQuote(quoteId: UUID): IncompleteQuote {
        val optionalQuote: Optional<IncompleteQuote> = incompleteQuoteRepository.findById(quoteId)
        if (!optionalQuote.isPresent) throw NullPointerException("No Incomplete quote found with id $quoteId")
        return optionalQuote.get()
    }
}

