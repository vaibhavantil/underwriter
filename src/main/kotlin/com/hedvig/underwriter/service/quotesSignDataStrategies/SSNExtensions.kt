package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.service.model.PersonPolicyHolder

fun List<Quote>.safelyGetSSN(): String {
    var ssn: String? = null
    this.forEach { quote ->
        if (quote.data is PersonPolicyHolder<*>) {
            quote.data.ssn?.let {
                ssn = it
                return@forEach
            }
        } else {
            throw RuntimeException("Quote data should not be able to be of type ${quote.data::class}")
        }
    }
    return ssn!!
}
