package com.hedvig.underwriter.graphql

import com.hedvig.graphql.commons.extensions.getAcceptLanguage
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.extensions.createCompleteQuoteResult
import com.hedvig.underwriter.extensions.createIncompleteQuoteResult
import com.hedvig.underwriter.extensions.firstName
import com.hedvig.underwriter.extensions.lastName
import com.hedvig.underwriter.extensions.validTo
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.model.Quote
import graphql.schema.DataFetchingEnvironment

fun Quote.getCompleteQuoteResult(
    env: DataFetchingEnvironment,
    localizationService: LocalizationService,
    textKeysLocaleResolver: TextKeysLocaleResolver
) = QuoteResult.CompleteQuote(
    id = id,
    firstName = firstName,
    lastName = lastName,
    currentInsurer = currentInsurer,
    price = MonetaryAmountV2(
        price!!.toPlainString(),
        "SEK"
    ),
    details = createCompleteQuoteResult(
        localizationService,
        textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
    ),
    expiresAt = validTo
)

fun Quote.getIncompleteQuoteResult(
    env: DataFetchingEnvironment,
    localizationService: LocalizationService,
    textKeysLocaleResolver: TextKeysLocaleResolver
) = QuoteResult.IncompleteQuote(
    id = id,
    firstName = firstName,
    lastName = lastName,
    currentInsurer = currentInsurer,
    details = createIncompleteQuoteResult(
        localizationService,
        textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
    )
)
