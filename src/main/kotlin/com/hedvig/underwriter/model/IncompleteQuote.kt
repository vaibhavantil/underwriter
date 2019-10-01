package com.hedvig.underwriter.model

import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.time.Instant
import java.time.LocalDate
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
        @Enumerated(EnumType.STRING)
        val quoteState: QuoteState = QuoteState.INCOMPLETE,
        val createdAt: Instant,
        @Enumerated(EnumType.STRING)
        val productType: ProductType = ProductType.UNKNOWN,
        @Enumerated(EnumType.STRING)
        var lineOfBusiness: LineOfBusiness?,

        @field:Type(type="jsonb")
        @field:Column(columnDefinition = "jsonb")
        val incompleteQuoteData: IncompleteQuoteData? = null,
        @Enumerated(EnumType.STRING)
        var quoteInitiatedFrom: QuoteInitiatedFrom?,
        var birthDate: LocalDate?,
        var livingSpace: Int?,
        var houseHoldSize: Int?,
        var isStudent: Boolean?,
        var ssn: String?
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

        fun complete(): CompleteQuote {
                return when {
                        this.incompleteQuoteData is IncompleteQuoteData.House -> {

                                CompleteQuote(
                                        incompleteQuote = this,
                                        quoteState = this.quoteState,
                                        quoteCreatedAt = Instant.now(),
                                        productType = this.productType,
                                        lineOfBusiness = this.lineOfBusiness!!,
                                        price = null,
                                        completeQuoteData = CompleteQuoteData.House(this.incompleteQuoteData.street!!,
                                                this.incompleteQuoteData.zipcode!!,
                                                this.incompleteQuoteData.city!!,
                                                this.incompleteQuoteData.livingSpace!!,
                                                this.incompleteQuoteData.householdSize!!
                                        ),
                                        quoteInitiatedFrom = this.quoteInitiatedFrom!!,
                                        birthDate = this.birthDate!!,
                                        livingSpace = this.livingSpace!!,
                                        houseHoldSize = this.houseHoldSize!!,
                                        isStudent = this.isStudent!!,
                                        ssn = this.ssn!!
                                )

                        }
                        this.incompleteQuoteData is IncompleteQuoteData.Home -> {
                                CompleteQuote(
                                        incompleteQuote = this,
                                        quoteState = this.quoteState,
                                        quoteCreatedAt = Instant.now(),
                                        productType = this.productType,
                                        lineOfBusiness = this.lineOfBusiness!!,
                                        price = null,
                                        completeQuoteData = CompleteQuoteData.Home(
                                                this.incompleteQuoteData.address!!,
                                                this.incompleteQuoteData.numberOfRooms!!,
                                                this.incompleteQuoteData.zipCode!!,
                                                this.incompleteQuoteData.floor!!
                                        ),
                                        quoteInitiatedFrom = this.quoteInitiatedFrom!!,
                                        birthDate = this.birthDate!!,
                                        livingSpace = this.livingSpace!!,
                                        houseHoldSize = this.houseHoldSize!!,
                                        isStudent = this.isStudent!!,
                                        ssn = this.ssn!!
                                )
                        }
                        this.incompleteQuoteData == null -> throw NullPointerException("Incomplete quote data cannot be null")
                        else -> throw RuntimeException("Incomplete quote is of unknown type: ${this.incompleteQuoteData::class}")
                }
        }

        companion object {

                private fun house(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote = IncompleteQuote (
                        quoteState = incompleteQuoteDto.quoteState,
                        createdAt = Instant.now(),
                        productType = ProductType.HOUSE,
                        lineOfBusiness = incompleteQuoteDto.lineOfBusiness,
                        quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom,
                        incompleteQuoteData = IncompleteQuoteData.House(
                                street = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.street,
                                zipcode = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.zipcode,
                                city = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.city,
                                livingSpace = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.livingSpace,
                                householdSize = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHouseQuoteDataDto?.householdSize
                        ),
                        birthDate = incompleteQuoteDto.birthDate,
                        livingSpace = incompleteQuoteDto.livingSpace,
                        houseHoldSize = incompleteQuoteDto.houseHoldSize,
                        isStudent = incompleteQuoteDto.isStudent,
                        ssn = incompleteQuoteDto.ssn
                )

                private fun home(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote = IncompleteQuote (
                        quoteState = incompleteQuoteDto.quoteState,
                        createdAt = Instant.now(),
                        productType = ProductType.HOME,
                        lineOfBusiness = incompleteQuoteDto.lineOfBusiness,
                        quoteInitiatedFrom = incompleteQuoteDto.quoteInitiatedFrom,
                        incompleteQuoteData = IncompleteQuoteData.Home(
                                address = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHomeQuoteDataDto?.address,
                                numberOfRooms = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHomeQuoteDataDto?.numberOfRooms,
                                zipCode = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHomeQuoteDataDto?.zipCode,
                                floor = incompleteQuoteDto.incompleteQuoteDataDto?.incompleteHomeQuoteDataDto?.floor
                        ),
                        birthDate = incompleteQuoteDto.birthDate,
                        livingSpace = incompleteQuoteDto.livingSpace,
                        houseHoldSize = incompleteQuoteDto.houseHoldSize,
                        isStudent = incompleteQuoteDto.isStudent,
                        ssn = incompleteQuoteDto.ssn
                )

                fun from(incompleteQuoteDto: IncompleteQuoteDto): IncompleteQuote {
                        if(incompleteQuoteDto.productType == ProductType.HOUSE) return house(incompleteQuoteDto)
                        if(incompleteQuoteDto.productType == ProductType.HOME) return home(incompleteQuoteDto)
                        throw RuntimeException("cannot create a incomplete quote with product type ${incompleteQuoteDto.productType}")
                }

        }
}




