package com.hedvig.underwriter.serviceIntegration.priceEngine

import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryRequest
import com.hedvig.underwriter.serviceIntegration.priceEngine.dtos.PriceQueryResponse

interface PriceEngineService {
    fun queryNorwegianHomeContentPrice(query: PriceQueryRequest.NorwegianHomeContent): PriceQueryResponse
    fun queryNorwegianTravelPrice(query: PriceQueryRequest.NorwegianTravel): PriceQueryResponse
    fun querySwedishApartmentPrice(query: PriceQueryRequest.SwedishApartment): PriceQueryResponse
    fun querySwedishHousePrice(query: PriceQueryRequest.SwedishHouse): PriceQueryResponse
}
