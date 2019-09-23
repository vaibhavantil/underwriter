package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.*
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import com.hedvig.underwriter.repository.IncompleteQuoteRepository
import com.hedvig.underwriter.web.Dtos.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.lang.NullPointerException
import java.time.Instant
import java.util.*
import java.util.stream.Collectors

@Service
class QuoteServiceImpl @Autowired constructor(
        val incompleteQuoteRepository: IncompleteQuoteRepository,
        val completeQuoteRepository: CompleteQuoteRepository
): QuoteService {
    override fun createIncompleteQuote(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuoteResponseDto {
        val incompleteQuote = incompleteQuoteRepository.save(IncompleteQuote.from(incompleteQuoteDto))
        return IncompleteQuoteResponseDto(incompleteQuote.id!!, incompleteQuote.productType, incompleteQuote.quoteInitiatedFrom)
    }

//    TODO: refactor
    override fun updateIncompleteQuoteData(incompleteQuoteDto: IncompleteQuoteDto, quoteId: UUID) {

        val incompleteQuote = getIncompleteQuote(quoteId)

        if (incompleteQuoteDto.lineOfBusiness != null) {
            incompleteQuote.lineOfBusiness = incompleteQuoteDto.lineOfBusiness
        }
        if (incompleteQuoteDto.quoteInitiatedFrom != null) {
            incompleteQuote.quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom
        }


        if (incompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteQuoteData.House) {
            val incompleteHouseQuoteDataDto: IncompleteHouseQuoteDataDto? = incompleteQuoteDto.incompleteQuoteDataDto.incompleteHouseQuoteDataDto
            val incompleteHouseQuoteData: IncompleteQuoteData.House = (incompleteQuote.incompleteQuoteData as IncompleteQuoteData.House)

            if (incompleteHouseQuoteDataDto?.zipcode != null) {
                incompleteHouseQuoteData.zipcode = incompleteHouseQuoteDataDto.zipcode
            }
            if (incompleteHouseQuoteDataDto?.city != null) {
                incompleteHouseQuoteData.city = incompleteHouseQuoteDataDto.city
            }
            if (incompleteHouseQuoteDataDto?.city != null) {
                incompleteHouseQuoteData.personalNumber = incompleteHouseQuoteDataDto.personalNumber
            }
            if (incompleteHouseQuoteDataDto?.street != null) {
                incompleteHouseQuoteData.street = incompleteHouseQuoteDataDto.street
            }
            if (incompleteHouseQuoteDataDto?.householdSize != null) {
                incompleteHouseQuoteData.householdSize = incompleteHouseQuoteDataDto.householdSize
            }
            if (incompleteHouseQuoteDataDto?.livingSpace != null) {
                incompleteHouseQuoteData.livingSpace = incompleteHouseQuoteDataDto.livingSpace
            }
        }

        if (incompleteQuoteDto.incompleteQuoteDataDto != null && incompleteQuote.incompleteQuoteData is IncompleteQuoteData.Home) {
            val incompleteHomeQuoteDataDto: IncompleteHomeQuoteDataDto? = incompleteQuoteDto.incompleteQuoteDataDto.incompleteHomeQuoteDataDto
            val incompleteHomeQuoteData: IncompleteQuoteData.Home = (incompleteQuote.incompleteQuoteData as IncompleteQuoteData.Home)

            if (incompleteHomeQuoteDataDto?.numberOfRooms != null) {
                incompleteHomeQuoteData.numberOfRooms = incompleteHomeQuoteDataDto.numberOfRooms
            }
            if (incompleteHomeQuoteDataDto?.address != null) {
                incompleteHomeQuoteData.address = incompleteHomeQuoteDataDto.address
            }
        }
        incompleteQuoteRepository.save(incompleteQuote)
    }

    override fun findIncompleteQuoteById(id: UUID): Optional<IncompleteQuote> {
        return incompleteQuoteRepository.findById(id)
    }

    override fun createCompleteQuote(incompleteQuoteId: UUID): CompleteQuoteResponseDto {

        val incompleteQuote = getIncompleteQuote(incompleteQuoteId)

            if (incompleteQuote.incompleteQuoteData is IncompleteQuoteData.House) {
                try {
                    val completeQuote = CompleteQuote(
                            quoteState = incompleteQuote.quoteState,
                            quoteCreatedAt = Instant.now(),
                            productType = incompleteQuote.productType,
                            lineOfBusiness = incompleteQuote.lineOfBusiness,
                            price = 100,
                            completeQuoteData = CompleteQuoteData.House(incompleteQuote.incompleteQuoteData.street!!,
                                    incompleteQuote.incompleteQuoteData.zipcode!!,
                                    incompleteQuote.incompleteQuoteData.city!!,
                                    incompleteQuote.incompleteQuoteData.livingSpace!!,
                                    incompleteQuote.incompleteQuoteData.personalNumber!!,
                                    incompleteQuote.incompleteQuoteData.householdSize!!
                            ),
                            quoteInitiatedFrom = incompleteQuote.quoteInitiatedFrom
                    )
                    completeQuoteRepository.save(completeQuote)

                    return CompleteQuoteResponseDto(completeQuote.id!!, completeQuote.price)
                } catch(exception: Exception) {
                    throw NullPointerException("Cannot create quote, info missing")
                }

            }

            if (incompleteQuote.incompleteQuoteData is IncompleteQuoteData.Home) {
                try {
                    val completeQuote = CompleteQuote(
                            quoteState = incompleteQuote.quoteState,
                            quoteCreatedAt = Instant.now(),
                            productType = incompleteQuote.productType,
                            lineOfBusiness = incompleteQuote.lineOfBusiness,
                            price = 250,
                            completeQuoteData = CompleteQuoteData.Home(incompleteQuote.incompleteQuoteData.address!!,
                                    incompleteQuote.incompleteQuoteData.numberOfRooms!!),
                            quoteInitiatedFrom = incompleteQuote.quoteInitiatedFrom
                    )

                    completeQuoteRepository.save(completeQuote)
                    return CompleteQuoteResponseDto(completeQuote.id!!, completeQuote.price)
                    } catch(exception: Exception) {
                        throw NullPointerException("Cannot create quote, info missing")
                }

            }
        return CompleteQuoteResponseDto(UUID.randomUUID(), 1)
    }

    private fun getIncompleteQuote(quoteId: UUID): IncompleteQuote {
        val optionalQuote: Optional<IncompleteQuote> = incompleteQuoteRepository.findById(quoteId)

        if(!optionalQuote.isPresent) {
            throw NullPointerException("No Incomplete quote found with id $quoteId")
        }
        return optionalQuote.get()
    }
}