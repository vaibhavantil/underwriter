package com.hedvig.underwriter.service

import com.hedvig.underwriter.graphql.type.BundledQuotes
import com.hedvig.underwriter.graphql.type.TypeMapper
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundleInsuranceCostRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundledPriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.ProductType
import org.javamoney.moneta.Money
import org.springframework.stereotype.Component
import java.util.Locale
import java.util.UUID

@Component
class BundleQuotesServiceImpl(
    private val quotesService: QuoteService,
    private val productPricingService: ProductPricingService,
    private val typeMapper: TypeMapper
) : BundleQuotesService {

    override fun bundleQuotes(memberId: String, ids: List<UUID>, locale: Locale): BundledQuotes {
        val quotes = quotesService.getQuotes(ids)

        val request =
            CalculateBundleInsuranceCostRequest(
                productsToBeBundled = quotes.map { quote ->
                    CalculateBundledPriceDto(
                        Money.of(
                            quote.price, when (quote.data) {
                                is SwedishHouseData,
                                is SwedishApartmentData -> "SEK"
                                is NorwegianHomeContentsData,
                                is NorwegianTravelData -> "NOK"
                            }
                        ), when (val data = quote.data) {
                            is SwedishHouseData -> ProductType.SWEDISH_HOUSE
                            is SwedishApartmentData -> if (data.isStudent) ProductType.SWEDISH_STUDENT_BRF else ProductType.SWEDISH_BRF
                            is NorwegianHomeContentsData -> if (data.isYouth) ProductType.NORWEGIAN_YOUTH_HOME_CONTENTS else ProductType.NORWEGIAN_HOME_CONTENTS
                            is NorwegianTravelData -> if (data.isYouth) ProductType.NORWEGIAN_YOUTH_TRAVEL else ProductType.NORWEGIAN_TRAVEL
                        }
                    )
                }
            )

        val insuranceCost = productPricingService.calculateBundleInsuranceCost(request, memberId)

        return BundledQuotes(quotes.map {
            typeMapper.mapToBundleQuote(
                it,
                locale
            )
        }, insuranceCost)
    }

}
