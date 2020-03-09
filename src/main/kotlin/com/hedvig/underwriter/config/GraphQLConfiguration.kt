package com.hedvig.underwriter.config

import com.coxautodev.graphql.tools.SchemaParserDictionary
import com.hedvig.underwriter.graphql.type.ExtraBuilding
import com.hedvig.underwriter.graphql.type.IncompleteQuoteDetails
import com.hedvig.underwriter.graphql.type.QuoteDetails
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.graphql.type.UnderwritingLimitsHit
import com.hedvig.underwriter.graphql.type.depricated.CompleteQuoteDetails
import com.hedvig.underwriter.service.model.StartSignResponse
import org.dataloader.DataLoader
import org.dataloader.DataLoaderRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GraphQLConfiguration {
    @Bean
    fun dataLoaderRegistry(loaderList: List<DataLoader<*, *>>): DataLoaderRegistry {
        val registry = DataLoaderRegistry()
        for (loader in loaderList) {
            registry.register(loader.javaClass.simpleName, loader)
        }
        return registry
    }

    @Bean
    fun schemaParserDictionary(): SchemaParserDictionary {
        return SchemaParserDictionary()
            .add(
                dictionary = listOf(
                    CompleteQuoteDetails.CompleteApartmentQuoteDetails::class.java,
                    CompleteQuoteDetails.CompleteHouseQuoteDetails::class.java,
                    CompleteQuoteDetails.UnknownQuoteDetails::class.java,
                    QuoteResult.CompleteQuote::class.java,
                    QuoteResult.IncompleteQuote::class.java,
                    UnderwritingLimitsHit::class.java,
                    QuoteDetails.SwedishApartmentQuoteDetails::class.java,
                    QuoteDetails.SwedishHouseQuoteDetails::class.java,
                    QuoteDetails.NorwegianHomeContentsDetails::class.java,
                    QuoteDetails.NorwegianTravelDetails::class.java,
                    IncompleteQuoteDetails.IncompleteApartmentQuoteDetails::class.java,
                    IncompleteQuoteDetails.IncompleteHouseQuoteDetails::class.java,
                    StartSignResponse.SwedishBankIdSession::class.java,
                    StartSignResponse.NorwegianBankIdSession::class.java,
                    StartSignResponse.FailedToStartSign::class.java,
                    ExtraBuilding.ExtraBuildingGarage::class.java,
                    ExtraBuilding.ExtraBuildingCarport::class.java,
                    ExtraBuilding.ExtraBuildingShed::class.java,
                    ExtraBuilding.ExtraBuildingStorehouse::class.java,
                    ExtraBuilding.ExtraBuildingFriggebod::class.java,
                    ExtraBuilding.ExtraBuildingAttefall::class.java,
                    ExtraBuilding.ExtraBuildingOuthouse::class.java,
                    ExtraBuilding.ExtraBuildingGuesthouse::class.java,
                    ExtraBuilding.ExtraBuildingGazebo::class.java,
                    ExtraBuilding.ExtraBuildingGreenhouse::class.java,
                    ExtraBuilding.ExtraBuildingSauna::class.java,
                    ExtraBuilding.ExtraBuildingBarn::class.java,
                    ExtraBuilding.ExtraBuildingBoathouse::class.java,
                    ExtraBuilding.ExtraBuildingOther::class.java
                )
            )
    }
}
