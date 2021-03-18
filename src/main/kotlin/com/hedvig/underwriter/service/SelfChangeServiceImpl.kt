package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SelfChangeServiceImpl(
    private val quoteRepository: QuoteRepository,
    private val productPricingService: ProductPricingService
) : SelfChangeService {
    override fun changeToQuotes(quoteIds: List<UUID>, memberId: String) {
        val quotes = quoteRepository.findQuotes(quoteIds)
        val result = productPricingService.selfChangeContracts(
            memberId = memberId,
            quotes = quotes
        )
        val updates = result.createdContracts + result.updatedContracts
        updates.forEach { contract ->
            val quote = quotes.first { contract.quoteId == it.id }
            quoteRepository.update(
                quote.copy(contractId = contract.contractId, agreementId = contract.agreementId)
            )
        }
    }
}
