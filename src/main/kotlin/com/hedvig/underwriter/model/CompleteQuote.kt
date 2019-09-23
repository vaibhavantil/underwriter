package com.hedvig.underwriter.model

import com.hedvig.underwriter.web.Dtos.CompleteQuoteDto
import com.hedvig.underwriter.web.Dtos.IncompleteQuoteDto
import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs
import java.lang.Exception
import java.time.Instant
import java.util.*
//import javax.money.MonetaryAmount
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
    val quoteState: QuoteState = QuoteState.QUOTED,
    val quoteCreatedAt: Instant,
    val productType: ProductType = ProductType.UNKNOWN,
    var lineOfBusiness: LineOfBusiness?,
    val price: Int = 0,

    @field:Type(type = "jsonb")
    @field:Column(columnDefinition = "jsonb")
    var completeQuoteData: CompleteQuoteData,
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
}






