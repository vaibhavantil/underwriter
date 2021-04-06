package com.hedvig.underwriter.testhelp.databuilder

import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.service.model.QuoteRequestData
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class DanishHomeContentsQuoteRequestBuilder(
    val id: UUID = UUID.fromString("ab5924e4-0c72-11ea-a337-4865ee119be4"),
    val firstName: String = "",
    val lastName: String = "",
    val ssn: String? = "2802640073",
    val birthDate: LocalDate = LocalDate.of(1964, 2, 28),
    val email: String = "em@i.l",
    val quotingPartner: Partner = Partner.HEDVIG,
    val memberId: String? = "123",
    val originatingProductId: UUID? = null,
    val startDate: Instant? = Instant.now(),
    val dataCollectionId: UUID? = null,
    val currentInsurer: String? = null,
    val data: DataBuilder<QuoteRequestData.DanishHomeContents> = DanishHomeContentsQuoteRequestDataBuilder(),
    val productType: ProductType? = ProductType.APARTMENT
) : DataBuilder<QuoteRequest> {
    override fun build(): QuoteRequest = QuoteRequest(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = null,
        currentInsurer = currentInsurer,
        birthDate = birthDate,
        ssn = ssn,
        quotingPartner = quotingPartner,
        productType = productType,
        incompleteQuoteData = data.build(),
        memberId = memberId,
        originatingProductId = originatingProductId,
        startDate = startDate,
        dataCollectionId = dataCollectionId
    )

    fun build(homeContentsData: QuoteRequestData.DanishHomeContents?, newSsn: String? = null): QuoteRequest = QuoteRequest(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phoneNumber = null,
        currentInsurer = currentInsurer,
        birthDate = birthDate,
        ssn = newSsn ?: ssn,
        quotingPartner = quotingPartner,
        productType = productType,
        incompleteQuoteData = homeContentsData ?: data.build(),
        memberId = memberId,
        originatingProductId = originatingProductId,
        startDate = startDate,
        dataCollectionId = dataCollectionId
    )
}
