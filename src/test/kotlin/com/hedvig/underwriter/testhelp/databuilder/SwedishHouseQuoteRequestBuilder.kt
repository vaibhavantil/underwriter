package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData
import java.time.Instant
import java.util.UUID

data class SwedishHouseQuoteRequestBuilder(
    val id: UUID = UUID.fromString("ab5924e4-0c72-11ea-a337-4865ee119be4"),
    val firstName: String = "",
    val lastName: String = "",
    val ssn: String = "191212121212",
    val email: String = "em@i.l",
    val quotingPartner: Partner = Partner.HEDVIG,
    val memberId: String? = null,
    val originatingProductId: UUID? = null,
    val startDate: Instant? = Instant.now(),
    val dataCollectionId: UUID? = null,
    val currentInsurer: String? = null,
    val data: DataBuilder<QuoteRequestData.SwedishHouse> = SwedishHouseQuoteRequestDataBuilder(),
    val productType: ProductType? = ProductType.APARTMENT
) : DataBuilder<QuoteRequest> {
    override fun build(): QuoteRequest = QuoteRequest(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = null,
        currentInsurer = currentInsurer,
        birthDate = ssn.birthDateFromSwedishSsn(),
        ssn = ssn,
        quotingPartner = quotingPartner,
        productType = productType,
        incompleteQuoteData = data.build(),
        memberId = memberId,
        originatingProductId = originatingProductId,
        startDate = startDate,
        dataCollectionId = dataCollectionId
    )
}
