package com.hedvig.underwriter.service

import arrow.core.Right
import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.model.ssn
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.service.quotesSignDataStrategies.RedirectSignStrategy
import com.hedvig.underwriter.service.quotesSignDataStrategies.SignStrategyService
import com.hedvig.underwriter.service.quotesSignDataStrategies.SimpleSignStrategy
import com.hedvig.underwriter.service.quotesSignDataStrategies.SwedishBankIdSignStrategy
import com.hedvig.underwriter.serviceIntegration.customerio.CustomerIO
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.NationalIdentification
import com.hedvig.underwriter.serviceIntegration.memberService.RedirectCountry
import com.hedvig.underwriter.serviceIntegration.memberService.UnderwriterStartSignSessionRequest
import com.hedvig.underwriter.serviceIntegration.memberService.UnderwriterStartSignSessionResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsMemberAlreadySignedResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.IsSsnAlreadySignedMemberResponse
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Nationality
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract.CreateContractResponse
import com.hedvig.underwriter.testhelp.databuilder.a
import com.hedvig.underwriter.web.dtos.SignQuoteFromHopeRequest
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.springframework.core.env.Environment
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import java.util.UUID

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

    lateinit var signStrategyService: SignStrategyService

    lateinit var swedishBankIdSignStrategy: SwedishBankIdSignStrategy
    lateinit var redirectSignStrategy: RedirectSignStrategy
    lateinit var simpleSignStrategy: SimpleSignStrategy

    @MockK
    lateinit var env: Environment

    lateinit var cut: SignService

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        swedishBankIdSignStrategy = SwedishBankIdSignStrategy(
            signSessionRepository, memberService
        )

        redirectSignStrategy = RedirectSignStrategy(
            signSessionRepository, memberService
        )
        simpleSignStrategy = SimpleSignStrategy(
            signSessionRepository, memberService
        )

        signStrategyService = SignStrategyService(
            swedishBankIdSignStrategy, redirectSignStrategy, simpleSignStrategy, env
        )

        cut = SignServiceImpl(
            quoteService,
            quoteRepository,
            memberService,
            productPricingService,
            signSessionRepository,
            signStrategyService,
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

        every { memberService.createMember() } returns "1234"
        every {
            productPricingService.createContractsFromQuotes(
                any(),
                any(),
                any()
            )
        } returns listOf(
            CreateContractResponse(
                agreementId = UUID.randomUUID(),
                quoteId = quoteId,
                contractId = UUID.randomUUID()
            )
        )
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

        every { memberService.createMember() } returns "1234"
        every {
            productPricingService.createContractsFromQuotes(
                any(),
                any(),
                any()
            )
        } returns listOf(
            CreateContractResponse(
                agreementId = UUID.randomUUID(),
                quoteId = quoteId,
                contractId = UUID.randomUUID()
            )
        )
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1234, true))
        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(false)
        every { env.activeProfiles } returns arrayOf<String>()

        cut.signQuote(quoteId, SignQuoteRequest(Name("", ""), LocalDate.now(), "null"))
        verify { customerIO.postSignUpdate(any()) }
    }

    @Test
    fun startSigningOfSwedishQuote_startSwedishSign() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0], memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference

        every {
            memberService.startSwedishBankIdSign(
                quote.memberId!!.toLong(),
                signSessionReference,
                NationalIdentification(quote.ssn, Nationality.SWEDEN),
                ipAddress,
                false
            )
        } returns UnderwriterStartSignSessionResponse.SwedishBankId(
            "autoStartToken"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 1) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.SwedishBankIdSession::class.java)
    }

    @Test
    fun startSigningOfSwedishQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote = a.QuoteBuilder(id = quoteIds[0], memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every {
            memberService.startSwedishBankIdSign(
                quote.memberId!!.toLong(),
                signSessionReference,
                NationalIdentification(quote.ssn, Nationality.SWEDEN),
                ipAddress,
                false
            )
        } returns UnderwriterStartSignSessionResponse.SwedishBankId(
            autoStartToken = null,
            internalErrorMessage = "Failed"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
        assertThat((result as StartSignResponse.FailedToStartSign).errorMessage).isEqualTo("Failed")
    }

    @Test
    fun startSigningOfSwedishQuotes_getQuoteStateNotSignableErrorOrNullReturnsError_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote = a.QuoteBuilder(
            id = quoteIds[0],
            memberId = memberId,
            state = QuoteState.EXPIRED
        ).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningOfNorwegianQuote_startNorwegianSign() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every {
            memberService.startRedirectBankIdSign(
                quote.memberId!!.toLong(),
                signSessionReference,
                NationalIdentification(quote.ssn, Nationality.NORWAY),
                successUrl,
                failUrl,
                RedirectCountry.NORWAY
            )
        } returns UnderwriterStartSignSessionResponse.BankIdRedirect(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, null, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.NorwegianBankIdSession::class.java)
    }

    @Test
    fun startSigningOfNorwegianQuotes_startNorwegianSign() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()
        val quote2 =
            a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianTravelDataBuilder(), memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every {
            memberService.startRedirectBankIdSign(
                quote.memberId!!.toLong(),
                signSessionReference,
                NationalIdentification(quote.ssn, Nationality.NORWAY),
                successUrl,
                failUrl,
                RedirectCountry.NORWAY
            )
        } returns UnderwriterStartSignSessionResponse.BankIdRedirect(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.NorwegianBankIdSession::class.java)
    }

    @Test
    fun startSigningOfSwedishAndNorwegianQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.SwedishHouseDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningOfOnlyDanishAccidentQuote_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishAccidentDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningOfOnlyDanishTravelQuote_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishTravelDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningOfDanishAccidentAndDanishTravelQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishAccidentDataBuilder(), memberId = memberId).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.DanishTravelDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningOfDanishHomeContentAndDanishAccidentQuotes_returnsDanishBankIdSession() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishHomeContentsDataBuilder(), memberId = memberId).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.DanishAccidentDataBuilder(), memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every {
            memberService.startRedirectBankIdSign(
                quote.memberId!!.toLong(),
                signSessionReference,
                NationalIdentification(quote.ssn, Nationality.DENMARK),
                successUrl,
                failUrl,
                RedirectCountry.DENMARK
            )
        } returns UnderwriterStartSignSessionResponse.BankIdRedirect(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.DanishBankIdSession::class.java)
    }

    @Test
    fun startSigningOfAllDanishTypeOfQuotes_returnsDanishBankIdSession() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishHomeContentsDataBuilder(), memberId = memberId).build()
        val quote2 = a.QuoteBuilder(id = quoteIds[1], data = a.DanishAccidentDataBuilder(), memberId = memberId).build()
        val quote3 = a.QuoteBuilder(id = quoteIds[2], data = a.DanishTravelDataBuilder(), memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2, quote3)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every {
            memberService.startRedirectBankIdSign(
                quote.memberId!!.toLong(),
                signSessionReference,
                NationalIdentification(quote.ssn, Nationality.DENMARK),
                successUrl,
                failUrl,
                RedirectCountry.DENMARK
            )
        } returns UnderwriterStartSignSessionResponse.BankIdRedirect(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.DanishBankIdSession::class.java)
    }

    @Test
    fun startSigningOfThreeDanishHomeContentQuotes_returnsFailedToStartSign() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishHomeContentsDataBuilder(), memberId = memberId).build()
        val quote2 =
            a.QuoteBuilder(id = quoteIds[1], data = a.DanishHomeContentsDataBuilder(), memberId = memberId).build()
        val quote3 =
            a.QuoteBuilder(id = quoteIds[2], data = a.DanishHomeContentsDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote, quote2, quote3)
        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningZeroQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf()

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
    }

    @Test
    fun startSigningThreeQuotes_returnsFailResponse() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote1 =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()
        val quote2 =
            a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()
        val quote3 =
            a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianTravelDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote1, quote2, quote3)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        verify(exactly = 0) { signSessionRepository.insert(any()) }
    }

    @Test
    fun failStartSignQuotesWithNoMemberId() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote1 =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = null).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote1)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
        assertThat((result as StartSignResponse.FailedToStartSign).errorMessage).isEqualTo(
            StartSignErrors.noMemberIdOnQuote.errorMessage
        )
        assertThat(result.errorCode).isEqualTo(
            StartSignErrors.noMemberIdOnQuote.errorCode
        )
    }

    @Test
    fun failStartSignQuotesWithDifferentMemberIdFromHedvigToken() {
        val quoteIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val quote1 =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()
        val quote2 =
            a.QuoteBuilder(id = quoteIds[1], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote1, quote2)

        val result = cut.startSigningQuotes(quoteIds, "1234", ipAddress, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
        assertThat((result as StartSignResponse.FailedToStartSign).errorMessage).isEqualTo(
            StartSignErrors.variousMemberId.errorMessage
        )
        assertThat(result.errorCode).isEqualTo(
            StartSignErrors.variousMemberId.errorCode
        )
    }

    @Test
    fun failStartSignNorwegianQuotesNoTargetUrls() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)

        val result = cut.startSigningQuotes(quoteIds, memberId, ipAddress, null, null)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
        assertThat((result as StartSignResponse.FailedToStartSign).errorMessage).isEqualTo(
            StartSignErrors.targetURLNotProvided.errorMessage
        )
        assertThat(result.errorCode).isEqualTo(
            StartSignErrors.targetURLNotProvided.errorCode
        )
    }

    @Test
    fun failStartSignIfMemberAlreadySigned() {
        every { memberService.isMemberIdAlreadySignedMemberEntity(memberId.toLong()) } returns IsMemberAlreadySignedResponse(
            true
        )

        val result = cut.startSigningQuotes(listOf(UUID.randomUUID()), memberId, ipAddress, null, null)

        assertThat(result).isInstanceOf(StartSignResponse.FailedToStartSign::class.java)
        assertThat((result as StartSignResponse.FailedToStartSign).errorMessage).isEqualTo(
            StartSignErrors.memberIsAlreadySigned.errorMessage
        )
        assertThat(result.errorCode).isEqualTo(
            StartSignErrors.memberIsAlreadySigned.errorCode
        )
    }

    @Test
    fun verifyThatMemberServiceIsNotTriggeredWhenSigningFromHope() {
        val quoteId = UUID.fromString("3D6D0502-5E40-4C90-9330-722132F69B94")
        val quote =
            a.QuoteBuilder(id = quoteId, data = a.NorwegianHomeContentDataBuilder(), memberId = memberId).build()

        every { memberService.isSsnAlreadySignedMemberEntity(any()) } returns IsSsnAlreadySignedMemberResponse(
            ssnAlreadySignedMember = true
        )
        every { quoteService.getQuotes(listOf(quoteId)) } returns listOf(quote)
        every { quoteRepository.find(any()) } returns quote
        every { quoteRepository.update(any(), any()) } returnsArgument 0
        every {
            productPricingService.createContractsFromQuotes(
                any(),
                any(),
                any()
            )
        } returns listOf(
            CreateContractResponse(
                agreementId = UUID.randomUUID(),
                quoteId = quoteId,
                contractId = UUID.randomUUID()
            )
        )
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1234, true))

        cut.signQuoteFromHope(
            quoteId,
            SignQuoteFromHopeRequest(activationDate = LocalDate.parse("2020-05-11"), token = null)
        )

        verify(inverse = true) {
            memberService.signQuote(
                memberId = memberId.toLong(),
                underwriterQuoteSignRequest = any()
            )
        }
    }

    @Test
    fun startSigningOfDanishQuote_startDanishSign() {
        val quoteIds = listOf(UUID.randomUUID())
        val quote =
            a.QuoteBuilder(id = quoteIds[0], data = a.DanishHomeContentsDataBuilder(), memberId = memberId).build()
        val signSessionReference = UUID.randomUUID()

        every { memberService.isMemberIdAlreadySignedMemberEntity(any()) } returns IsMemberAlreadySignedResponse(false)
        every { quoteService.getQuotes(quoteIds) } returns listOf(quote)
        every { signSessionRepository.insert(quoteIds) } returns signSessionReference
        every {
            memberService.startRedirectBankIdSign(
                memberId.toLong(),
                signSessionReference,
                NationalIdentification("1212120000", Nationality.DENMARK),
                successUrl,
                failUrl,
                RedirectCountry.DENMARK
            )
        } returns UnderwriterStartSignSessionResponse.BankIdRedirect(
            "redirect url"
        )

        val result = cut.startSigningQuotes(quoteIds, memberId, null, successUrl, failUrl)

        assertThat(result).isInstanceOf(StartSignResponse.DanishBankIdSession::class.java)
    }

    companion object {
        private val memberId = "1337"
        private val ipAddress = "127.0.0.1"
        private val successUrl = "http://hedvig.com"
        private val failUrl = "http://hedvig.com"
    }
}
