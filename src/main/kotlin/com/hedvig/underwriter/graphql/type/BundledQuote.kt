package com.hedvig.underwriter.graphql.type

import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.underwriter.util.Pii
import java.time.LocalDate
import java.util.UUID

data class BundledQuote(
    val id: UUID,
    @Pii val firstName: String,
    @Pii val lastName: String,
    val currentInsurer: CurrentInsurer?,
    @Pii val ssn: String?,
    val birthDate: LocalDate,
    val price: MonetaryAmountV2,
    val quoteDetails: QuoteDetails,
    val startDate: LocalDate?,
    val expiresAt: LocalDate,
    @Pii val email: String?,
    val dataCollectionId: UUID?
) {
    val typeOfContract: ContractAgreementType
        get() = quoteDetails.typeOfContract
}
