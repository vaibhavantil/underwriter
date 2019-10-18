package com.hedvig.underwriter.service

import arrow.core.Right
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.Name
import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.ProductType
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.serviceIntegration.customerio.CustomerIO
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UnderwriterQuoteSignResponse
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RapioProductCreatedResponseDto
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class QuoteServiceImplTest {

    @MockK
    lateinit var debtChecker: DebtChecker

    @MockK
    lateinit var memberService: MemberService

    @MockK
    lateinit var productPricingService: ProductPricingService

    @MockK
    lateinit var quoteRepository: QuoteRepository

    @MockK
    lateinit var customerIO: CustomerIO

    @Before
    fun setUp() = MockKAnnotations.init(this, relaxUnitFun = true)

    @Test
    fun givenPartnerSendsPartnerIdToCustomerIO() {

        val cut = QuoteServiceImpl(debtChecker, memberService, productPricingService, quoteRepository, customerIO)

        val quoteId = UUID.randomUUID()
        val quote = Quote(
            quoteId,
            Instant.now(),
            BigDecimal.ZERO,
            ProductType.APARTMENT,
            QuoteInitiatedFrom.RAPIO,
            Partner.COMPRICER,
            ApartmentData(
                UUID.randomUUID(),
                ssn = "191212121212",
                lastName = "",
                livingSpace = 2,
                city = "",
                zipCode = "",
                householdSize = 3,
                subType = ApartmentProductSubType.BRF,
                firstName = "",
                street = ""
            ),
            null,
            null
        )

        every { quoteRepository.find(any()) } returns quote
        every { memberService.createMember() } returns "1234"
        every {
            productPricingService.createProduct(
                any(),
                any()
            )
        } returns RapioProductCreatedResponseDto(UUID.randomUUID())
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1234, true))

        cut.signQuote(quoteId, SignQuoteRequest(Name("", ""), LocalDate.now(), "null"))
        verify { customerIO.setPartnerCode("1234", Partner.COMPRICER) }
    }

    @Test
    fun givenPartnerIsHedvigDoNotSendPartnerIdToCustomerIO() {

        val cut = QuoteServiceImpl(debtChecker, memberService, productPricingService, quoteRepository, customerIO)

        val quoteId = UUID.randomUUID()
        val quote = Quote(
            quoteId,
            Instant.now(),
            BigDecimal.ZERO,
            ProductType.APARTMENT,
            QuoteInitiatedFrom.RAPIO,
            Partner.HEDVIG,
            ApartmentData(
                UUID.randomUUID(),
                ssn = "191212121212",
                lastName = "",
                livingSpace = 2,
                city = "",
                zipCode = "",
                householdSize = 3,
                subType = ApartmentProductSubType.BRF,
                firstName = "",
                street = ""
            ),
            null,
            null
        )

        every { quoteRepository.find(any()) } returns quote
        every { memberService.createMember() } returns "1234"
        every {
            productPricingService.createProduct(
                any(),
                any()
            )
        } returns RapioProductCreatedResponseDto(UUID.randomUUID())
        every { memberService.signQuote(any(), any()) } returns Right(UnderwriterQuoteSignResponse(1234, true))

        cut.signQuote(quoteId, SignQuoteRequest(Name("", ""), LocalDate.now(), "null"))
        verify(exactly = 0) { customerIO.setPartnerCode(any(), any()) }
    }
}
