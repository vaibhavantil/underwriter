package com.hedvig.underwriter.graphql.type

import java.util.UUID

data class SignQuotesInput(
    val quoteIds: List<UUID>,
    val targetUrl: String?,
    val failedTargetUrl: String?
)
