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
        if (incompleteQuoteDto.firstName != null) incompleteQuote.firstName = incompleteQuoteDto.firstName
        if (incompleteQuoteDto.lastName != null) incompleteQuote.lastName = incompleteQuoteDto.lastName
        if (incompleteQuoteDto.currentInsurer != null) incompleteQuote.currentInsurer = incompleteQuoteDto.currentInsurer
        if (incompleteQuoteDto.livingSpace != null) incompleteQuote.livingSpace = incompleteQuoteDto.livingSpace
        if (incompleteQuoteDto.houseHoldSize != null) incompleteQuote.houseHoldSize = incompleteQuoteDto.houseHoldSize
        if (incompleteQuoteDto.isStudent != null) incompleteQuote.isStudent = incompleteQuoteDto.isStudent
        if (incompleteQuoteDto.ssn != null) incompleteQuote.ssn = incompleteQuoteDto.ssn

        if (incompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteQuoteData.House) {
            val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto? = incompleteQuoteDto.incompleteQuoteDataDto.incompleteHouseQuoteDataDto
            val incompleteHouseQuoteData: IncompleteQuoteData.House = incompleteQuote.incompleteQuoteData

            if (incompleteHouseQuoteDataDto?.zipcode != null) incompleteHouseQuoteData.zipCode = incompleteHouseQuoteDataDto.zipcode
            if (incompleteHouseQuoteDataDto?.city != null) incompleteHouseQuoteData.city = incompleteHouseQuoteDataDto.city
            if (incompleteHouseQuoteDataDto?.street != null) incompleteHouseQuoteData.street = incompleteHouseQuoteDataDto.street
            if (incompleteHouseQuoteDataDto?.householdSize != null) incompleteHouseQuoteData.householdSize = incompleteHouseQuoteDataDto.householdSize
            if (incompleteHouseQuoteDataDto?.livingSpace != null) incompleteHouseQuoteData.livingSpace = incompleteHouseQuoteDataDto.livingSpace
        }

        if (incompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteQuoteData.Home) {
            val incompleteHomeQuoteDataDto: IncompleteHomeQuoteDataDto? = incompleteQuoteDto.incompleteQuoteDataDto.incompleteHomeQuoteDataDto
            val incompleteHomeQuoteData: IncompleteQuoteData.Home = (incompleteQuote.incompleteQuoteData as IncompleteQuoteData.Home)

            if (incompleteHomeQuoteDataDto?.numberOfRooms != null) incompleteHomeQuoteData.numberOfRooms = incompleteHomeQuoteDataDto.numberOfRooms
            if (incompleteHomeQuoteDataDto?.street != null) incompleteHomeQuoteData.street = incompleteHomeQuoteDataDto.street
            if (incompleteHomeQuoteDataDto?.city != null) incompleteHomeQuoteData.city = incompleteHomeQuoteDataDto.city
            if (incompleteHomeQuoteDataDto?.zipCode != null) incompleteHomeQuoteData.zipCode = incompleteHomeQuoteDataDto.zipCode
            if (incompleteHomeQuoteDataDto?.floor != null) incompleteHomeQuoteData.floor = incompleteHomeQuoteDataDto.floor
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

