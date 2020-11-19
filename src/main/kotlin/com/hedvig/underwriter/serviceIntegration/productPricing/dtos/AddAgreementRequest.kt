package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.productPricingObjects.dtos.AgreementQuote
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers.AgreementQuoteMapper
import com.hedvig.underwriter.web.dtos.AddAgreementFromQuoteRequest
import java.time.LocalDate
import java.util.UUID

data class AddAgreementRequest(
    val contractId: UUID?,
    val quoteFromAgreementId: UUID?,
    val previousAgreementToDate: LocalDate?,
    val quote: AgreementQuote
) {
    companion object {
        fun from(
            quote: Quote,
            request: AddAgreementFromQuoteRequest
        ) = AddAgreementRequest(
            contractId = request.contractId,
            quoteFromAgreementId = quote.originatingProductId,
            previousAgreementToDate = request.previousAgreementActiveTo,
            quote = AgreementQuoteMapper.toQuote(
                quote = quote,
                fromDate = request.activeFrom,
                toDate = request.activeTo
            )
        )
    }
}
