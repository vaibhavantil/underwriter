package com.hedvig.underwriter.service

import com.hedvig.underwriter.graphql.type.QuoteBundle
import com.hedvig.underwriter.graphql.type.TypeMapper
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundleInsuranceCostRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.CalculateBundledPriceDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.InsuranceType
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

    override fun bundleQuotes(memberId: String, ids: List<UUID>, locale: Locale): QuoteBundle {
        val quotes = quotesService.getQuotes(ids)

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
                        is DanishHomeContentsData -> InsuranceType.DANISH_PLACEHOLDER
                    }
                )
            }
        )

        val insuranceCost = productPricingService.calculateBundleInsuranceCost(request, memberId)

        return QuoteBundle(quotes.map {
            typeMapper.mapToBundleQuote(
                it,
                locale
            )
        }, insuranceCost)
    }
}
