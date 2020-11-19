package com.hedvig.underwriter.service.exceptions

import com.hedvig.underwriter.service.guidelines.GuidelineBreached

class QuoteCompletionFailedException(message: String, val breachedUnderwritingGuidelines: List<GuidelineBreached>? = null) : RuntimeException(message)
