package com.hedvig.underwriter.util

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import java.util.concurrent.ConcurrentHashMap

open class MetricsCounter(open val registry: MeterRegistry, val name: String) {

    private val counters = ConcurrentHashMap<String, Counter>()

    fun increment(vararg tags: String?) {
        val key = tags.joinToString(":")

        val counter = counters[key] ?: run {
            val counter = registry.counter(name, *tags)
            counters[key] = counter
            counter
        }

        counter.increment()
    }
}
