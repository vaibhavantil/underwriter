package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.*
import com.hedvig.underwriter.repository.IncompleteQuoteRepository
import com.hedvig.underwriter.web.Dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.lang.NullPointerException
import java.util.*

@Service
class QuoteBuilderServiceImpl @Autowired constructor(
        val incompleteQuoteRepository: IncompleteQuoteRepository
) : QuoteBuilderService {

    override fun createIncompleteQuote(incompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuoteResponseDto {
        val incompleteQuote = incompleteQuoteRepository.save(IncompleteQuote.from(incompleteQuoteDto))
        return IncompleteQuoteResponseDto(incompleteQuote.id!!, incompleteQuote.productType, incompleteQuote.quoteInitiatedFrom)
    }

    //    TODO: refactor
    override fun updateIncompleteQuoteData(incompleteincompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID) {

        val incompleteQuote = getIncompleteQuote(quoteId)

        if (incompleteincompleteQuoteDto.lineOfBusiness != null) incompleteQuote.lineOfBusiness = incompleteincompleteQuoteDto.lineOfBusiness
        if (incompleteincompleteQuoteDto.quoteInitiatedFrom != null) incompleteQuote.quoteInitiatedFrom = incompleteincompleteQuoteDto.quoteInitiatedFrom
        if (incompleteincompleteQuoteDto.isStudent != null) incompleteQuote.isStudent = incompleteincompleteQuoteDto.isStudent
        if (incompleteincompleteQuoteDto.ssn != null) incompleteQuote.ssn = incompleteincompleteQuoteDto.ssn
        if (incompleteincompleteQuoteDto.firstName != null) incompleteQuote.firstName = incompleteincompleteQuoteDto.firstName
        if (incompleteincompleteQuoteDto.lastName != null) incompleteQuote.lastName = incompleteincompleteQuoteDto.lastName
        if (incompleteincompleteQuoteDto.currentInsurer != null) incompleteQuote.currentInsurer = incompleteincompleteQuoteDto.currentInsurer
        if(incompleteincompleteQuoteDto.quoteInitiatedFrom != null) incompleteQuote.quoteInitiatedFrom = incompleteincompleteQuoteDto.quoteInitiatedFrom

        if (incompleteincompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteHouseData) {
            val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto? = incompleteincompleteQuoteDto.incompleteQuoteDataDto.incompleteHouseQuoteDataDto
            val incompleteIncompleteHouseDataQuoteData: IncompleteHouseData = incompleteQuote.incompleteQuoteData as IncompleteHouseData

            if (incompleteHouseQuoteDataDto?.zipCode != null) incompleteIncompleteHouseDataQuoteData.zipCode = incompleteHouseQuoteDataDto.zipCode
            if (incompleteHouseQuoteDataDto?.city != null) incompleteIncompleteHouseDataQuoteData.city = incompleteHouseQuoteDataDto.city
            if (incompleteHouseQuoteDataDto?.street != null) incompleteIncompleteHouseDataQuoteData.street = incompleteHouseQuoteDataDto.street
            if (incompleteHouseQuoteDataDto?.householdSize != null) incompleteIncompleteHouseDataQuoteData.householdSize = incompleteHouseQuoteDataDto.householdSize
            if (incompleteHouseQuoteDataDto?.livingSpace != null) incompleteIncompleteHouseDataQuoteData.livingSpace = incompleteHouseQuoteDataDto.livingSpace
        }

        if (incompleteincompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteHomeData) {
            val incommingData: IncompleteHomeQuoteDataDto? = incompleteincompleteQuoteDto.incompleteQuoteDataDto.incompleteHomeQuoteDataDto
            var incompleteIncompleteHomeDataQuoteData: IncompleteHomeData = incompleteQuote.incompleteQuoteData as IncompleteHomeData

            if (incommingData?.street != null) incompleteIncompleteHomeDataQuoteData = incompleteIncompleteHomeDataQuoteData.copy(street = incommingData.street)
            if (incommingData?.zipCode != null) incompleteIncompleteHomeDataQuoteData = incompleteIncompleteHomeDataQuoteData.copy(zipCode = incommingData.zipCode)
            if (incommingData?.city != null) incompleteIncompleteHomeDataQuoteData = incompleteIncompleteHomeDataQuoteData.copy(city  = incommingData.city)
            if (incommingData?.livingSpace != null) incompleteIncompleteHomeDataQuoteData = incompleteIncompleteHomeDataQuoteData.copy(livingSpace = incommingData.livingSpace)
            if (incommingData?.houseHoldSize != null) incompleteIncompleteHomeDataQuoteData = incompleteIncompleteHomeDataQuoteData.copy(householdSize = incommingData.houseHoldSize)

            incompleteQuote.incompleteQuoteData = incompleteIncompleteHomeDataQuoteData
        }

        incompleteQuoteRepository.save(incompleteQuote)
    }

    override fun findIncompleteQuoteById(id: UUID): Optional<IncompleteQuote> {
        return incompleteQuoteRepository.findById(id)
    }

    override fun getIncompleteQuote(quoteId: UUID): IncompleteQuote {
        val optionalQuote: Optional<IncompleteQuote> = incompleteQuoteRepository.findById(quoteId)
        if (!optionalQuote.isPresent) throw NullPointerException("No Incomplete quote found with id $quoteId")
        return optionalQuote.get()
    }
}

