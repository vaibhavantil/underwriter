package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
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

class a {

    data class QuoteBuilder(
        val id: UUID = UUID.fromString("1c3463c4-0c71-11ea-8fd9-4865ee119be4"),
        val createdAt: Instant = Instant.now(),
        val price: BigDecimal? = BigDecimal.ZERO,
        val productType: ProductType = ProductType.APARTMENT,
        val state: QuoteState = QuoteState.INCOMPLETE,
        val initiatedFrom: QuoteInitiatedFrom = QuoteInitiatedFrom.RAPIO,
        val attributedTo: Partner = Partner.HEDVIG,
        val data: QuoteDataBuilder = ApartmentDataBuilder(),

        val currentInsurer: String? = null,

        val startDate: LocalDate? = null,

        val validity: Long = ONE_DAY * 30,
        val breachedUnderwritingGuidelines: List<String>? = null,
        val underwritingGuidelinesBypassedBy: String? = null,
        val memberId: String? = null,
        val originatingProductId: UUID? = null,
        val signedProductId: UUID? = null

    ) {
        fun w(quoteData: QuoteDataBuilder? = null): QuoteBuilder {
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
            signedProductId
        )
    }

    interface QuoteDataBuilder {
        fun build(): QuoteData
    }

    data class ApartmentDataBuilder(
        val id: UUID = UUID.fromString("ab5924e4-0c72-11ea-a337-4865ee119be4"),
        val ssn: String? = "191212121212",
        val firstName: String? = "",
        val lastName: String? = "",
        val email: String? = "em@i.l",

        val street: String? = "",
        val city: String? = "",
        val zipCode: String? = "",
        val householdSize: Int? = 3,
        val livingSpace: Int? = 2,
        val subType: ApartmentProductSubType? = ApartmentProductSubType.BRF,
        val internalId: Int? = null
    ) : QuoteDataBuilder {

        override fun build() = ApartmentData(id, ssn, firstName, lastName, email, street, city, zipCode, householdSize, livingSpace, subType, internalId)
    }
}
