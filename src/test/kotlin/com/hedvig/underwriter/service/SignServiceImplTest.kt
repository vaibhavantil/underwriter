package com.hedvig.underwriter.service

import arrow.core.Right
import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.serviceIntegration.customerio.CustomerIO
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.StartNorwegianBankIdSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.StartSwedishBankIdSignResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.SignedProductResponseDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.SignedQuoteRequest
import com.hedvig.underwriter.testhelp.databuilder.a
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import java.time.LocalDate
import java.util.UUID
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity

@RunWith(MockitoJUnitRunner::class)
class SignServiceImplTest {

    @MockK
    lateinit var quoteService: QuoteService

    @MockK
    lateinit var underwriter: Underwriter

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var productPricingService: ProductPricingService

    @MockK
    lateinit var quoteRepository: QuoteRepository

    @MockK
    lateinit var signSessionRepository: SignSessionRepository

    @MockK
    lateinit var customerIO: CustomerIO

    @MockK
    lateinit var env: Environment

    lateinit var cut: SignService

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        cut = SignServiceImpl(
            quoteService,
            quoteRepository,
            memberService,
            productPricingService,
            signSessionRepository,
            customerIO,
            env
        )
    }

    @Test
    fun givenPartnerSendsPartnerIdToCustomerIO() {
        val quoteId = UUID.randomUUID()
        val quote = a.QuoteBuilder(id = quoteId, attributedTo = Partner.COMPRICER).build()

        every { quoteRepository.find(any()) } returns quote
        every { quoteRepository.update(any(), any()) } returnsArgument 0
        every { quoteService.getQuoteStateNotSignableErrorOrNull(any()) } returns null

        every { memberService.createMember() } returns "1234"
        every {
            productPricingService.signedQuote(
                any<SignedQuoteRequest>(),
                any()
            )
        } returns SignedProductResponseDto(UUID.randomUUID())
        every { productPricingService.redeemCampaign(any()) } returns ResponseEntity.ok().build()
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1234, true))
        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { env.activeProfiles } returns arrayOf<String>()

        cut.signQuote(quoteId, SignQuoteRequest(Name("", ""), LocalDate.now(), "null"))
        verify { customerIO.postSignUpdate(ofType(Quote::class)) }
    }

    @Test
    fun givenPartnerIsHedvigSendPartnerIdToCustomerIO() {
        val quoteId = UUID.randomUUID()
        val quote = a.QuoteBuilder(attributedTo = Partner.HEDVIG).build()

        every { quoteRepository.find(any()) } returns quote
        every { quoteRepository.update(any(), any()) } returnsArgument 0
        every { quoteService.getQuoteStateNotSignableErrorOrNull(any()) } returns null

        every { memberService.createMember() } returns "1234"
        every {
            productPricingService.signedQuote(
                any<SignedQuoteRequest>(),
                any()
            )
        } returns SignedProductResponseDto(UUID.randomUUID())
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1234, true))
        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { env.activeProfiles } returns arrayOf<String>()

        cut.signQuote(quoteId, SignQuoteRequest(Name("", ""), LocalDate.now(), "null"))
        verify { customerIO.postSignUpdate(any()) }
    }

    @Test
    fun startSigningOfSwedishQuote_startSwedishSign() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0]).build()
        val signSessionReference = UUID.randomUUID()

        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every { memberService.startSwedishBankIdSignQuotes(signSessionReference) } returns StartSwedishBankIdSignResponse(
            "autoStartToken"
        )

        val result = cut.startSigningQuotes(quoteIds)

        verify(exactly = 1) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.SwedishBankIdSession::class.java)
    }

    @Test
    fun startSigningOfSwedishQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0]).build()
        val signSessionReference = UUID.randomUUID()

        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every { memberService.startSwedishBankIdSignQuotes(signSessionReference) } returns StartSwedishBankIdSignResponse(
            autoStartToken = null,
            internalErrorMessage = "Failed"
        )

        val result = cut.startSigningQuotes(quoteIds)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
        assertThat((result as StartSignResponse.FailedToStartSign).errorMessage).isEqualTo("Failed")
    }

    @Test
    fun startSigningOfNorwegianQuote_startNorwegianSign() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder()).build()
        val signSessionReference = UUID.randomUUID()

        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every { memberService.startNorwegianBankIdSignQuotes(signSessionReference) } returns StartNorwegianBankIdSignResponse(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds)

        assertThat(result).isInstanceOf(StartSignResponse.NorwegianBankIdSession::class.java)
    }

    @Test
    fun startSigningOfNorwegianQuotes_startNorwegianSign() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder()).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianTravelDataBuilder()).build()
        val signSessionReference = UUID.randomUUID()

        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every { memberService.startNorwegianBankIdSignQuotes(signSessionReference) } returns StartNorwegianBankIdSignResponse(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds)

        assertThat(result).isInstanceOf(StartSignResponse.NorwegianBankIdSession::class.java)
    }

    @Test
    fun startSigningOfSwedishAndNorwegianQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder()).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.SwedishHouseDataBuilder()).build()

        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2)

        val result = cut.startSigningQuotes(quoteIds)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningZeroQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())

        every { quoteService.getQuotes(quoteIds) } returns listOf()

        val result = cut.startSigningQuotes(quoteIds)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningThreeQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote1 = a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder()).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianHomeContentDataBuilder()).build()
        val quote3 = a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianTravelDataBuilder()).build()

        every { quoteService.getQuotes(quoteIds) } returns listOf(quote1, quote2, quote3)

        val result = cut.startSigningQuotes(quoteIds)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }
}
