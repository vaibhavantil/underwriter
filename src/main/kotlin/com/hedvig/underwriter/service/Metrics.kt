package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.Market
import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class Metrics(private val registry: MeterRegistry) {

    private val breachedUnderwritingGuidelineCounters = mutableMapOf<Market, MutableMap<String, Counter>>()

    fun increment(market: Market, breachedGuidelineCode: String) {
        val marketMap = breachedUnderwritingGuidelineCounters[market] ?: run {
            val marketMap = mutableMapOf<String, Counter>()
            breachedUnderwritingGuidelineCounters[market] = marketMap
            marketMap
        }

        val counter = marketMap[breachedGuidelineCode] ?: run {
            val counter = registry.counter(counterName, "market", market.name, "breachedGuideline", breachedGuidelineCode)
            marketMap[breachedGuidelineCode] = counter
            breachedUnderwritingGuidelineCounters[market] = marketMap
            counter
        }

        counter.increment()
    }

    companion object {
        private const val counterName = "breached.underwriting.guidelines"
    }
}
