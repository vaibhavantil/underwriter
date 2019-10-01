package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.*
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.repository.IncompleteQuoteRepository
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.HomeQuotePriceDto
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
    override fun createIncompleteQuote(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuoteResponseDto {
        val incompleteQuote = incompleteQuoteRepository.save(IncompleteQuote.from(incompleteQuoteDto))
        return IncompleteQuoteResponseDto(incompleteQuote.id!!, incompleteQuote.productType, incompleteQuote.quoteInitiatedFrom)
    }

    //    TODO: refactor
    override fun updateIncompleteQuoteData(incompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID) {

        val incompleteQuote = getIncompleteQuote(quoteId)

        if (incompleteQuoteDto.lineOfBusiness != null) incompleteQuote.lineOfBusiness = incompleteQuoteDto.lineOfBusiness
        if (incompleteQuoteDto.quoteInitiatedFrom != null) incompleteQuote.quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom
        if (incompleteQuoteDto.birthDate != null) incompleteQuote.birthDate = incompleteQuoteDto.birthDate
        if (incompleteQuoteDto.livingSpace != null) incompleteQuote.livingSpace = incompleteQuoteDto.livingSpace
        if (incompleteQuoteDto.houseHoldSize != null) incompleteQuote.houseHoldSize = incompleteQuoteDto.houseHoldSize
        if (incompleteQuoteDto.isStudent != null) incompleteQuote.isStudent = incompleteQuoteDto.isStudent
        if (incompleteQuoteDto.ssn != null) incompleteQuote.ssn = incompleteQuoteDto.ssn

        if (incompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteQuoteData.House) {
            val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto? = incompleteQuoteDto.incompleteQuoteDataDto.incompleteHouseQuoteDataDto
            val incompleteHouseQuoteData: IncompleteQuoteData.House = incompleteQuote.incompleteQuoteData

            if (incompleteHouseQuoteDataDto?.zipcode != null) incompleteHouseQuoteData.zipcode = incompleteHouseQuoteDataDto.zipcode
            if (incompleteHouseQuoteDataDto?.city != null) incompleteHouseQuoteData.city = incompleteHouseQuoteDataDto.city
            if (incompleteHouseQuoteDataDto?.street != null) incompleteHouseQuoteData.street = incompleteHouseQuoteDataDto.street
            if (incompleteHouseQuoteDataDto?.householdSize != null) incompleteHouseQuoteData.householdSize = incompleteHouseQuoteDataDto.householdSize
            if (incompleteHouseQuoteDataDto?.livingSpace != null) incompleteHouseQuoteData.livingSpace = incompleteHouseQuoteDataDto.livingSpace
        }

        if (incompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteQuoteData.Home) {
            val incompleteHomeQuoteDataDto: IncompleteHomeQuoteDataDto? = incompleteQuoteDto.incompleteQuoteDataDto.incompleteHomeQuoteDataDto
            val incompleteHomeQuoteData: IncompleteQuoteData.Home = (incompleteQuote.incompleteQuoteData as IncompleteQuoteData.Home)

            if (incompleteHomeQuoteDataDto?.numberOfRooms != null) incompleteHomeQuoteData.numberOfRooms = incompleteHomeQuoteDataDto.numberOfRooms
            if (incompleteHomeQuoteDataDto?.address != null) incompleteHomeQuoteData.address = incompleteHomeQuoteDataDto.address
            if (incompleteHomeQuoteDataDto?.zipCode != null) incompleteHomeQuoteData.zipCode = incompleteHomeQuoteDataDto.zipCode
            if (incompleteHomeQuoteDataDto?.floor != null) incompleteHomeQuoteData.floor = incompleteHomeQuoteDataDto.floor
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

