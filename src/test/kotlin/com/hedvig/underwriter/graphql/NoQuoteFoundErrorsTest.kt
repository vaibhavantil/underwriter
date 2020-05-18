package com.hedvig.underwriter.graphql

import com.hedvig.graphql.commons.extensions.getTokenOrNull
import com.hedvig.localization.service.LocalizationService
import com.hedvig.localization.service.TextKeysLocaleResolverImpl
import com.hedvig.underwriter.graphql.type.TypeMapper
import com.hedvig.underwriter.service.BundleQuotesService
import com.hedvig.underwriter.service.QuoteService
import graphql.schema.DataFetchingEnvironment
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class NoQuoteFoundErrorsTest {
    @MockK
    lateinit var quoteService: QuoteService

    @MockK
    lateinit var bundleQuoteService: BundleQuotesService

    @MockK
    lateinit var localizationService: LocalizationService

    @MockK
    lateinit var dataFetchingEnvironment: DataFetchingEnvironment

    @get:Rule
    val thrown = ExpectedException.none()

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `lastest quote finds no quote`() {

        val query =
            Query(quoteService, bundleQuoteService, TextKeysLocaleResolverImpl(), TypeMapper(localizationService))

        every { dataFetchingEnvironment.getTokenOrNull() } returns "1337"
        every { quoteService.getLatestQuoteForMemberId(any()) } returns null

        thrown.expect(QuoteNotFoundQueryException::class.java)
        query.lastQuoteOfMember(dataFetchingEnvironment)
    }
}
