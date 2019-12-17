package com.hedvig.underwriter.graphql

import com.hedvig.graphql.commons.extensions.getAcceptLanguage
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.extensions.createCompleteQuoteResult
import com.hedvig.underwriter.extensions.createIncompleteQuoteResult
import com.hedvig.underwriter.extensions.firstName
import com.hedvig.underwriter.extensions.lastName
import com.hedvig.underwriter.extensions.ssn
import com.hedvig.underwriter.extensions.validTo
import com.hedvig.underwriter.graphql.type.CurrentInsurer
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.util.toStockholmLocalDate
import graphql.schema.DataFetchingEnvironment
import java.time.LocalDate

fun Quote.getCompleteQuoteResult(
    env: DataFetchingEnvironment,
    localizationService: LocalizationService,
    textKeysLocaleResolver: TextKeysLocaleResolver,
    insuranceCost: InsuranceCost
) = QuoteResult.CompleteQuote(
    id = id,
    firstName = firstName,
    lastName = lastName,
    currentInsurer = currentInsurer?.let { CurrentInsurer.create(it) },
    ssn = ssn,
    price = MonetaryAmountV2(
        price!!.toPlainString(),
        "SEK"
    ),
    insuranceCost = insuranceCost,
    details = createCompleteQuoteResult(
        localizationService,
        textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
    ),
    expiresAt = validTo.toStockholmLocalDate(),
    startDate = startDate?.coerceAtLeast(LocalDate.now())
)

fun Quote.getIncompleteQuoteResult(
    env: DataFetchingEnvironment,
    localizationService: LocalizationService,
    textKeysLocaleResolver: TextKeysLocaleResolver
) = QuoteResult.IncompleteQuote(
    id = id,
    firstName = firstName,
    lastName = lastName,
    currentInsurer = currentInsurer?.let { CurrentInsurer.create(it) },
    details = createIncompleteQuoteResult(
        localizationService,
        textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
    ),
    startDate = startDate?.coerceAtLeast(LocalDate.now())
)
