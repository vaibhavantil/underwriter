package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.DanishAccidentData
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.BundleQuotesService.BundledQuotes
import com.hedvig.underwriter.service.quotesSignDataStrategies.SignStrategyService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundleInsuranceCostRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundledPriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.InsuranceType
import org.javamoney.moneta.Money
import org.springframework.stereotype.Component
import java.lang.IllegalArgumentException
import java.util.UUID

@Component
class BundleQuotesServiceImpl(
    private val quotesService: QuoteService,
    private val productPricingService: ProductPricingService,
    private val signStrategyService: SignStrategyService
) : BundleQuotesService {

    override fun bundleQuotes(memberId: String?, ids: List<UUID>): BundledQuotes {
        val quotes = quotesService.getQuotes(ids)

        require(ids.isNotEmpty()) { "Not a bundle: $ids" }
        require(quotes.size == ids.size) { "Not all quotes found for $ids: ${quotes.map { it.id}.toList()}" }

        signStrategyService.validateBundling(quotes)?.let { throw IllegalArgumentException("Bundling not supported for quotes: ${it.errorMessage}") }

        val request = CalculateBundleInsuranceCostRequest(
            toBeBundled = quotes.map { quote ->
                CalculateBundledPriceDto(
                    grossPrice = Money.of(quote.price, quote.currency),
                    insuranceType = when (val data = quote.data) {
                        is SwedishHouseData -> InsuranceType.SWEDISH_HOUSE
                        is SwedishApartmentData -> when (data.subType!!) {
                            ApartmentProductSubType.BRF,
                            ApartmentProductSubType.STUDENT_BRF -> if (data.isStudent) InsuranceType.SWEDISH_STUDENT_BRF else InsuranceType.SWEDISH_BRF
                            ApartmentProductSubType.RENT,
                            ApartmentProductSubType.STUDENT_RENT -> if (data.isStudent) InsuranceType.SWEDISH_STUDENT_RENT else InsuranceType.SWEDISH_RENT
                        }
                        is NorwegianHomeContentsData -> if (data.isYouth) InsuranceType.NORWEGIAN_YOUTH_HOME_CONTENTS else InsuranceType.NORWEGIAN_HOME_CONTENTS
                        is NorwegianTravelData -> if (data.isYouth) InsuranceType.NORWEGIAN_YOUTH_TRAVEL else InsuranceType.NORWEGIAN_TRAVEL
                        is DanishHomeContentsData -> if (data.isStudent) InsuranceType.DANISH_STUDENT_HOME_CONTENTS else InsuranceType.DANISH_HOME_CONTENTS
                        is DanishAccidentData -> if (data.isStudent) InsuranceType.DANISH_STUDENT_ACCIDENT else InsuranceType.DANISH_ACCIDENT
                        is DanishTravelData -> if (data.isStudent) InsuranceType.DANISH_STUDENT_TRAVEL else InsuranceType.DANISH_TRAVEL
                    }
                )
            }
        )

        val insuranceCost =
            if (memberId != null) {
                productPricingService.calculateBundleInsuranceCostForMember(request, memberId)
            } else {
                productPricingService.calculateBundleInsuranceCost(request)
            }

        return BundledQuotes(quotes, insuranceCost)
    }
}
