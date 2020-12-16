package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ONE_DAY
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteState
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

fun quote(init: QB.() -> Unit): Quote {
    val qb = QB()
    qb.init()
    return qb.build()
}

class QB(
    var id: UUID = UUID.fromString("1c3463c4-0c71-11ea-8fd9-4865ee119be4"),
    var createdAt: Instant = Instant.now(),
    var price: BigDecimal? = BigDecimal.ZERO,
    var productType: ProductType = ProductType.APARTMENT,
    var state: QuoteState = QuoteState.INCOMPLETE,
    var initiatedFrom: QuoteInitiatedFrom = QuoteInitiatedFrom.RAPIO,
    var attributedTo: Partner = Partner.HEDVIG,
    var data: DataBuilder<QuoteData> = ApartmentDataBuilder(),

    var currentInsurer: String? = null,

    var startDate: LocalDate? = null,

    var validity: Long = ONE_DAY * 30,
    var breachedUnderwritingGuidelines: List<String>? = null,
    var underwritingGuidelinesBypassedBy: String? = null,
    var memberId: String? = null,
    var originatingProductId: UUID? = null,
    var agreementId: UUID? = null,
    var contractId: UUID? = null
) {
    fun build() = Quote(
        id,
        createdAt,
        price,
        productType,
        state,
        initiatedFrom,
        attributedTo,
        data.build(),
        currentInsurer,
        startDate,
        validity,
        breachedUnderwritingGuidelines,
        underwritingGuidelinesBypassedBy,
        memberId,
        originatingProductId,
        agreementId,
        null,
        null,
        contractId
    )
}

data class QuoteBuilder(
    val id: UUID = UUID.fromString("1c3463c4-0c71-11ea-8fd9-4865ee119be4"),
    val createdAt: Instant = Instant.now(),
    val price: BigDecimal? = BigDecimal.ZERO,
    val productType: ProductType = ProductType.APARTMENT,
    val state: QuoteState = QuoteState.INCOMPLETE,
    val initiatedFrom: QuoteInitiatedFrom = QuoteInitiatedFrom.RAPIO,
    val attributedTo: Partner = Partner.HEDVIG,
    val data: DataBuilder<QuoteData> = ApartmentDataBuilder(),

    val currentInsurer: String? = null,

    val startDate: LocalDate? = null,

    val validity: Long = ONE_DAY * 30,
    val breachedUnderwritingGuidelines: List<String>? = null,
    val underwritingGuidelinesBypassedBy: String? = null,
    val memberId: String? = null,
    val originatingProductId: UUID? = null,
    val agreementId: UUID? = null,
    val contractId: UUID? = null

) {
    fun w(quoteData: DataBuilder<QuoteData>? = null): QuoteBuilder {
        return this.copy(data = quoteData ?: this.data)
    }

    fun build() = Quote(
        id,
        createdAt,
        price,
        productType,
        state,
        initiatedFrom,
        attributedTo,
        data.build(),
        currentInsurer,
        startDate,
        validity,
        breachedUnderwritingGuidelines,
        underwritingGuidelinesBypassedBy,
        memberId,
        originatingProductId,
        agreementId,
        null,
        null,
        contractId
    )
}
