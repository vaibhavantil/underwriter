package com.hedvig.underwriter.model

import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.persistence.*

@Entity
@TypeDefs(
        TypeDef(name = "json", typeClass = JsonStringType::class),
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class))
class CompleteQuote (
        @field:Id
        @field:GeneratedValue(generator = "UUID")
        @field:GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
        var id: UUID? = null,

        @OneToOne
        val incompleteQuote: IncompleteQuote,

        @Enumerated(EnumType.STRING)
        val quoteState: QuoteState = QuoteState.QUOTED,
        val quoteCreatedAt: Instant,

        @Enumerated(EnumType.STRING)
        val productType: ProductType = ProductType.UNKNOWN,

        @Enumerated(EnumType.STRING)
        val lineOfBusiness: LineOfBusiness,
        var price: BigDecimal?,

        @field:Type(type = "jsonb")
        @field:Column(columnDefinition = "jsonb")
        val completeQuoteData: CompleteQuoteData,

        @Enumerated(EnumType.STRING)
        val quoteInitiatedFrom: QuoteInitiatedFrom,

        val birthDate: LocalDate,
        val livingSpace: Int,
        val houseHoldSize: Int,
        val isStudent: Boolean,
        val ssn: String
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
}






