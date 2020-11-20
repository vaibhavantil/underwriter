package com.hedvig.underwriter.service.exceptions

import com.hedvig.underwriter.service.guidelines.BreachedGuideline

class QuoteCompletionFailedException(message: String, val breachedUnderwritingGuidelines: List<BreachedGuideline>? = null) : RuntimeException(message)
