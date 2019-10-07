package com.hedvig.underwriter.model

import com.hedvig.underwriter.web.Dtos.PostIncompleteQuoteRequest
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
        var quoteState: QuoteState = QuoteState.INCOMPLETE,
        val createdAt: Instant,
        @Enumerated(EnumType.STRING)
        val productType: ProductType = ProductType.UNKNOWN,
        @Enumerated(EnumType.STRING)
        var lineOfBusiness: LineOfBusiness?,

        @field:Type(type="jsonb")
        @field:Column(columnDefinition = "jsonb")
        var incompleteQuoteData: IncompleteQuoteData? = null,
        @Enumerated(EnumType.STRING)
        var quoteInitiatedFrom: QuoteInitiatedFrom?,
        var firstName: String?,
        var lastName: String?,
        var currentInsurer: String?,
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
                val incompleteData = this.incompleteQuoteData
                return when(incompleteData) {
                        is IncompleteHouseData -> {


                                CompleteQuote(
                                        incompleteQuote = this,
                                        quoteState = this.quoteState,
                                        quoteCreatedAt = Instant.now(),
                                        productType = this.productType,
                                        lineOfBusiness = this.lineOfBusiness!!,
                                        price = null,
                                        completeQuoteData = CompleteQuoteData.of(incompleteData),
                                        quoteInitiatedFrom = this.quoteInitiatedFrom!!,
                                        firstName = this.firstName!!,
                                        lastName = this.lastName!!,
                                        currentInsurer = this.currentInsurer!!,
                                        livingSpace = incompleteData.livingSpace!!,
                                        houseHoldSize = incompleteData.householdSize!!,
                                        isStudent = this.isStudent!!,
                                        ssn = this.ssn!!
                                )

                        }
                        is IncompleteHomeData -> {
                                val completeQuote = CompleteQuote(
                                        incompleteQuote = this,
                                        quoteState = this.quoteState,
                                        quoteCreatedAt = Instant.now(),
                                        productType = this.productType,
                                        lineOfBusiness = this.lineOfBusiness!!,
                                        price = null,
                                        completeQuoteData = CompleteQuoteData.of(incompleteData),
                                        quoteInitiatedFrom = this.quoteInitiatedFrom!!,
                                        firstName = this.firstName,
                                        lastName = this.lastName,
                                        currentInsurer = this.currentInsurer,
                                        livingSpace = incompleteData.livingSpace!!,
                                        houseHoldSize = incompleteData.householdSize!!,
                                        isStudent = this.isStudent?:false,
                                        ssn = this.ssn!!
                                )
                                completeQuote
                        }
                        null -> throw NullPointerException("Incomplete quote data cannot be null")
                }
        }

        companion object {

                fun from(incompleteQuoteDto: PostIncompleteQuoteRequest): IncompleteQuote {
                        return IncompleteQuote(
                                quoteState = QuoteState.INCOMPLETE,
                                createdAt = Instant.now(),
                                productType = incompleteQuoteDto.incompleteQuoteDataDto!!.productType(),
                                lineOfBusiness = incompleteQuoteDto.lineOfBusiness,
                                quoteInitiatedFrom = QuoteInitiatedFrom.PARTNER,
                                incompleteQuoteData = incompleteQuoteDto.incompleteQuoteDataDto,
                                isStudent = null, //(incompleteQuoteDto.incompleteQuoteDataDto).isStudent,
                                ssn = incompleteQuoteDto.ssn,
                                currentInsurer = null,
                                firstName = null,
                                lastName = null,
                                id = UUID.randomUUID()
                        )
                }

        }
}




