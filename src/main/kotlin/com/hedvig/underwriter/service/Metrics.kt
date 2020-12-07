package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.Market
import com.hedvig.underwriter.service.guidelines.NorwegianHomeContentsGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianPersonGuidelines
import com.hedvig.underwriter.service.guidelines.NorwegianTravelGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishApartmentGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishHouseGuidelines
import com.hedvig.underwriter.service.guidelines.SwedishPersonalGuidelines
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class Metrics(private val registry: MeterRegistry, depthChecker: DebtChecker) {

    private val breachedUnderwritingGuidelineCounters: Map<Market, Map<String, Counter>>

    init {
        val mutableMap = mutableMapOf<Market, Map<String, Counter>>()

        mutableMap[Market.SWEDEN] = createMapForMarket(Market.SWEDEN, setOfBreachedSwedishCodes(depthChecker))
        mutableMap[Market.NORWAY] = createMapForMarket(Market.NORWAY, setOfBreachedNorwegianCodes())

        breachedUnderwritingGuidelineCounters = mutableMap.toMap()
    }

    private fun setOfBreachedSwedishCodes(depthChecker: DebtChecker): MutableSet<String> {
        val swedishBreachedCodes =
            SwedishApartmentGuidelines.setOfRules.map { it.breachedGuideline.code }.toMutableSet()
        swedishBreachedCodes.addAll(SwedishHouseGuidelines.setOfRules.map { it.breachedGuideline.code })
        swedishBreachedCodes.addAll(SwedishPersonalGuidelines(depthChecker).setOfRules.map { it.breachedGuideline.code })
        return swedishBreachedCodes
    }

    private fun setOfBreachedNorwegianCodes(): MutableSet<String> {
        val norwegianBreachedCodes =
            NorwegianHomeContentsGuidelines.setOfRules.map { it.breachedGuideline.code }.toMutableSet()
        norwegianBreachedCodes.addAll(NorwegianTravelGuidelines.setOfRules.map { it.breachedGuideline.code })
        norwegianBreachedCodes.addAll(NorwegianPersonGuidelines.setOfRules.map { it.breachedGuideline.code })
        return norwegianBreachedCodes
    }

    private fun createMapForMarket(market: Market, breachedCodes: MutableSet<String>): Map<String, Counter> {
        val mutableMap = mutableMapOf<String, Counter>()
        breachedCodes.forEach { code ->
            mutableMap[code] = registry.counter(counterName, "market", market.name, "breachedGuideline", code)
        }
        return mutableMap.toMap()
    }

    fun increment(market: Market, breachedGuidelineCode: String) {
        val marketMap = breachedUnderwritingGuidelineCounters[market] ?: run {
            logger.error("No counter for market: $market, when trying to increment breached guideline: $breachedGuidelineCode")
            return
        }

        marketMap[breachedGuidelineCode]?.increment() ?: run {
            logger.error("No counter for breached guideline: $breachedGuidelineCode, when tying to increment on market $market")
        }
    }

    companion object {
        private const val counterName = "breached.underwriting.guidelines"
        private val logger = LoggerFactory.getLogger(this::class.java)!!
    }
}
