package com.hedvig.underwriter.serviceIntegration.priceEngine

import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryRequest
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryResponse
import org.springframework.stereotype.Service

@Service
class PriceEngineServiceImpl(
    private val priceEngineClient: PriceEngineClient
) : PriceEngineService {

    override fun queryNorwegianHomeContentPrice(query: PriceQueryRequest.NorwegianHomeContent): PriceQueryResponse {
        return priceEngineClient.queryPrice(query)
    }

    override fun queryNorwegianTravelPrice(query: PriceQueryRequest.NorwegianTravel): PriceQueryResponse {
        return priceEngineClient.queryPrice(query)
    }

    override fun querySwedishApartmentPrice(
        query: PriceQueryRequest.SwedishApartment
    ): PriceQueryResponse {
        return priceEngineClient.queryPrice(query)
    }

    override fun querySwedishHousePrice(
        query: PriceQueryRequest.SwedishHouse
    ): PriceQueryResponse {
        return priceEngineClient.queryPrice(query)
    }

    override fun queryDanishHomeContentPrice(query: PriceQueryRequest.DanishHomeContent): PriceQueryResponse {
        return priceEngineClient.queryPrice(query)
    }
}
