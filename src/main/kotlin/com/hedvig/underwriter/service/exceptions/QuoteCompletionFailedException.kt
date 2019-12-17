package com.hedvig.underwriter.service.exceptions

class QuoteCompletionFailedException(message: String, val breachedUnderwritingGuidelines: List<String>? = null) : RuntimeException(message)
