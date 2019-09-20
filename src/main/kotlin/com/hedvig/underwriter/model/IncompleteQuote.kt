package com.hedvig.underwriter.model

import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
@TypeDefs(
        TypeDef(name = "json", typeClass = JsonStringType::class),
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class))
class IncompleteQuote (
        @field:Id
        @field:GeneratedValue(generator = "UUID")
        @field:GenericGenerator(name = "UUID",strategy = "org.hibernate.id.UUIDGenerator")
        var id: UUID? = null,
        var quoteState: QuoteState = QuoteState.INCOMPLETE,
        var dateStartedRecievingQuoteInfo: Instant,
        val productType: ProductType = ProductType.UNKNOWN,
        var lineOfBusiness: LineOfBusiness?,

        @field:Type(type="jsonb")
        @field:Column(columnDefinition = "jsonb")
        var incompleteQuoteData: IncompleteQuoteData? = null,
        var quoteInitiatedFrom: QuoteInitiatedFrom?
) {

        override fun hashCode(): Int {
                return 31
        }

        override fun equals(other: Any?): Boolean {
                return when {
                        this === other -> true
                        other == null -> false
                        IncompleteQuote::class.isInstance(other) && this.id == (other as IncompleteQuote).id -> true
                        else -> false
                }
        }

        companion object {

                private fun house(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote = IncompleteQuote (
                        quoteState = incompleteQuoteDto.quoteState,
                        dateStartedRecievingQuoteInfo = Instant.now(),
                        productType = ProductType.HOUSE,
                        lineOfBusiness = incompleteQuoteDto.lineOfBusiness,
                        quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom,
                        incompleteQuoteData = IncompleteQuoteData.House(
                                street = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.street,
                                zipcode = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.zipcode,
                                city = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.city,
                                livingSpace = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.livingSpace,
                                personalNumber = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.personalNumber,
                                householdSize = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.householdSize
                        )
                )

                private fun home(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote = IncompleteQuote (
                        quoteState = incompleteQuoteDto.quoteState,
                        dateStartedRecievingQuoteInfo = Instant.now(),
                        productType = ProductType.HOME,
                        lineOfBusiness = incompleteQuoteDto.lineOfBusiness,
                        quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom,
                        incompleteQuoteData = IncompleteQuoteData.Home(
                                address = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHomeQuoteDataDto?.address,
                                numberOfRooms = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHomeQuoteDataDto?.numberOfRooms
                        )
                )

                private fun genericQuote(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote = IncompleteQuote (
                        quoteState = incompleteQuoteDto.quoteState,
                        dateStartedRecievingQuoteInfo = Instant.now(),
                        productType = ProductType.HOME,
                        lineOfBusiness = incompleteQuoteDto.lineOfBusiness,
                        quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom,
                        incompleteQuoteData = null
                )

                fun from(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote {
                        if(incompleteQuoteDto.productType == ProductType.HOUSE) return house(incompleteQuoteDto)
                        if(incompleteQuoteDto.productType == ProductType.HOME) return home(incompleteQuoteDto)
                        return genericQuote(incompleteQuoteDto)
                }

        }
}




