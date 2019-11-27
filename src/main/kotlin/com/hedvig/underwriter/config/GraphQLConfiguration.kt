package com.hedvig.underwriter.config

import com.coxautodev.graphql.tools.SchemaParserDictionary
import com.hedvig.underwriter.graphql.type.ExtraBuilding
import com.hedvig.underwriter.graphql.type.QuoteDetails
import com.hedvig.underwriter.graphql.type.QuoteResult
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
                    QuoteResult.Quote::class.java,
                    QuoteResult.UnderwritingLimitsHit::class.java,
                    QuoteDetails.ApartmentQuoteDetails::class.java,
                    QuoteDetails.HouseQuoteDetails::class.java,
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
