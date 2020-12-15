package com.hedvig.underwriter.service.exceptions

import com.hedvig.underwriter.service.guidelines.BreachedGuidelineCode

class QuoteCompletionFailedException(
    message: String,
    val breachedUnderwritingGuidelines: List<BreachedGuidelineCode>? = null
) : RuntimeException(message)
