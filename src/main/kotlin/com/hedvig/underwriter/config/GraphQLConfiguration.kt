package com.hedvig.underwriter.config

import com.coxautodev.graphql.tools.SchemaParserDictionary
import com.hedvig.underwriter.graphql.QuoteDetails
import com.hedvig.underwriter.graphql.QuoteResult
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
                    QuoteDetails.HouseQuoteDetails::class.java
                    )
            )
    }
}
