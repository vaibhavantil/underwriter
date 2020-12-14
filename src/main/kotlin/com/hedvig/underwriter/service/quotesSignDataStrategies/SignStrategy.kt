package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.service.model.StartSignResponse

interface SignStrategy {
    fun startSign(quotes: List<Quote>, signData: SignData): StartSignResponse
}
