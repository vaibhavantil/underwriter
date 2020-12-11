package com.hedvig.underwriter.service

import arrow.core.Either
import arrow.core.Right
import arrow.core.flatMap
import arrow.core.toOption
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.model.SignSessionRepository
import com.hedvig.underwriter.service.exceptions.QuoteNotFoundException
import com.hedvig.underwriter.service.model.CompleteSignSessionData
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.service.model.StartSignErrors
import com.hedvig.underwriter.service.model.StartSignResponse
import com.hedvig.underwriter.service.quotesSignDataStrategies.SignData
import com.hedvig.underwriter.service.quotesSignDataStrategies.SignStrategyService
import com.hedvig.underwriter.serviceIntegration.customerio.CustomerIO
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RedeemCampaignDto
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import com.hedvig.underwriter.web.dtos.SignQuoteFromHopeRequest
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Service
class SignServiceImpl(
    val quoteService: QuoteService,
    // FIXME: SignService should only use quoteService in my opinion, but I'm not up for doing that refactor now.
    val quoteRepository: QuoteRepository,
    val memberService: MemberService,
    val productPricingService: ProductPricingService,
    val signSessionRepository: SignSessionRepository,
    val signStrategyService: SignStrategyService,
    val customerIO: CustomerIO,
    val env: Environment
) : SignService {

    override fun startSigningQuotes(
        quoteIds: List<UUID>,
        memberId: String,
        ipAddress: String?,
        successUrl: String?,
        failUrl: String?
    ): StartSignResponse {

        if (memberService.isMemberIdAlreadySignedMemberEntity(memberId.toLong()).memberAlreadySigned) {
            return StartSignErrors.memberIsAlreadySigned
        }

        val quotes = quoteService.getQuotes(quoteIds)
        quotes.forEach { quote ->
            quote.memberId?.let { quoteMemberId ->
                if (memberId != quoteMemberId) {
                    logger.info("Member [id: $memberId] tried to sign quote with member id: $quoteMemberId. [Quotes: $quotes]")
                    return StartSignErrors.variousMemberId
                }
            } ?: run {
                logger.info("Member [id: $memberId] tried to sign quote without member id. [Quotes: $quotes]")
                return StartSignErrors.noMemberIdOnQuote
            }

            val quoteNotSignableErrorDto = assertQuoteIsNotSignedOrExpired(quote)
            if (quoteNotSignableErrorDto != null) {
                return StartSignErrors.fromErrorResponse(quoteNotSignableErrorDto)
            }
        }

        return signStrategyService.startSign(
            quotes = quotes,
            signData = SignData(ipAddress, successUrl, failUrl)
        )
    }

    override fun completedSignSession(signSessionId: UUID, completeSignSessionData: CompleteSignSessionData) {
        val quoteIds = signSessionRepository.find(signSessionId)
        val quotes = quoteRepository.findQuotes(quoteIds)

        val createContractResponse = when (completeSignSessionData) {
            is CompleteSignSessionData.SwedishBankIdDataComplete ->
                productPricingService.createContractsFromQuotes(
                    quotes,
                    SignRequest(
                        referenceToken = completeSignSessionData.referenceToken,
                        signature = completeSignSessionData.signature,
                        oscpResponse = completeSignSessionData.oscpResponse
                    ),
                    token = null
                )
            is CompleteSignSessionData.NoMandate ->
                productPricingService.createContractsFromQuotesNoMandate(quotes)
        }
        quotes.forEach { quote ->
            val response = createContractResponse.first { quote.id == it.quoteId }
            redeemAndSignQuoteAndPostToCustomerio(quote, response.agreementId, true, response.contractId)
        }
    }

    private fun redeemAndSignQuoteAndPostToCustomerio(
        quote: Quote,
        agreementId: UUID,
        shouldCompleteSignInMemberService: Boolean,
        contractId: UUID
    ): Either<Nothing, SignedQuoteResponseDto> {
        val signedAt = Instant.now()
        return Right(
            quoteRepository.update(
                quote.copy(agreementId = agreementId, contractId = contractId)
            )
        )
            .map {
                redeemCampaigns(it)
            }
            .map {
                completeInMemberService(it, shouldCompleteSignInMemberService)
            }
            .map {
                quoteRepository.update(it.copy(state = QuoteState.SIGNED), signedAt)
            }
            .map {
                updateCustomerIO(it)
            }
            .map {
                checkNotNull(it.memberId) { "Quote must have a member id! Quote id: ${it.id}" }
                SignedQuoteResponseDto(contractId, it.memberId, signedAt)
            }
    }

    private fun updateCustomerIO(quote: Quote): Quote {
        try {
            customerIO.postSignUpdate(quote)
        } catch (ex: Exception) {
            logger.error(
                "Something went wrong while posting a signing update to customerIO " +
                    "[SignQuote: $quote]"
            )
        }
        return quote
    }

    private fun completeInMemberService(
        quote: Quote,
        shouldCompleteSignInMemberService: Boolean
    ): Quote {
        checkNotNull(quote.memberId) { "Quote must have a member id! Quote id: ${quote.id}" }
        if (shouldCompleteSignInMemberService && quote.data is PersonPolicyHolder<*>) {
            memberService.signQuote(
                quote.memberId.toLong(),
                UnderwriterQuoteSignRequest(quote.data.ssn!!)
            )
        }
        return quote
    }

    private fun redeemCampaigns(
        quote: Quote
    ): Quote {
        checkNotNull(quote.memberId) { "Quote must have a member id! Quote id: ${quote.id}" }
        quote.attributedTo.campaignCode?.let { campaignCode ->
            try {
                productPricingService.redeemCampaign(
                    RedeemCampaignDto(
                        quote.memberId,
                        campaignCode,
                        LocalDate.now(quote.getTimeZoneId())
                    )
                )
            } catch (e: FeignException) {
                logger.error("Failed to redeem $campaignCode for partner ${quote.attributedTo} with response ${e.message}")
            }
        }
        return quote
    }

    override fun signQuote(
        quoteId: UUID,
        body: SignQuoteRequest
    ): Either<ErrorResponseDto, SignedQuoteResponseDto> = quoteRepository.find(quoteId)
        .toOption()
        .toEither { ErrorResponseDto(ErrorCodes.NO_SUCH_QUOTE, "No such quote $quoteId") }
        .map(::assertAgreementIdIsNotNull)
        .map { updateNameFromRequest(it, body) }
        .map { updateStartTimeFromRequest(it, body) }
        .flatMap { createMemberMaybe(it) }
        .flatMap {
            signQuoteWithMemberId(
                it,
                true,
                SignRequest("", "", ""),
                body.email
            )
        }

    private fun createMemberMaybe(
        quote: Quote
    ): Either<ErrorResponseDto, Quote> {
        if (quote.memberId == null) {
            require(quote.data is PersonPolicyHolder<*>)

            val quoteNotSignableErrorDto = assertQuoteIsNotSignedOrExpired(quote)
            if (quoteNotSignableErrorDto != null) {
                return Either.left(quoteNotSignableErrorDto)
            }
            val memberAlreadySigned = checkIfMemberHasSignedInsurance(quote)

            if (memberAlreadySigned) {
                return Either.Left(
                    ErrorResponseDto(
                        ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE,
                        "quote is already signed"
                    )
                )
            }
            val memberId = memberService.createMember()

            memberService.updateMemberSsn(memberId.toLong(), UpdateSsnRequest(ssn = quote.data.ssn!!))

            return Either.Right(quoteRepository.update(quote.copy(memberId = memberId)))
        } else {
            return Either.Right(quote)
        }
    }

    override fun signQuoteFromHope(
        completeQuoteId: UUID,
        request: SignQuoteFromHopeRequest
    ): Either<ErrorResponseDto, SignedQuoteResponseDto> {
        val quote = quoteRepository.find(completeQuoteId)
            ?: throw QuoteNotFoundException("Quote $completeQuoteId not found when trying to sign")

        assertAgreementIdIsNotNull(quote)

        if (quote.memberId == null) {
            Either.Left(
                ErrorResponseDto(
                    ErrorCodes.MEMBER_ID_IS_NOT_PROVIDED,
                    "No member id connected to quote ${quote.id}"
                )
            )
        }

        val quoteNotSignableErrorDto = assertQuoteIsNotSignedOrExpired(quote)
        if (quoteNotSignableErrorDto != null) {
            return Either.left(quoteNotSignableErrorDto)
        }

        val memberAlreadySigned = checkIfMemberHasSignedInsurance(quote)

        if (!memberAlreadySigned) {
            return Either.Left(
                ErrorResponseDto(
                    ErrorCodes.MEMBER_DOES_NOT_HAVE_EXISTING_SIGNED_INSURANCE,
                    "Member does not have an existing signed insurance [QuoteId: ${quote.id}]"
                )
            )
        }

        val updatedQuote = quote.copy(startDate = request.activationDate, signFromHopeTriggeredBy = request.token)

        return signQuoteWithMemberId(
            updatedQuote,
            false,
            SignRequest("", "", ""),
            (quote.data as PersonPolicyHolder<*>).email!!
        )
    }

    private fun checkIfMemberHasSignedInsurance(
        quote: Quote
    ): Boolean {
        return when (quote.data) {
            is PersonPolicyHolder<*> -> memberService.isSsnAlreadySignedMemberEntity(quote.data.ssn!!).ssnAlreadySignedMember
            else -> throw RuntimeException("Unsupported quote data class")
        }
    }

    override fun memberSigned(memberId: String, signedRequest: SignRequest) {
        quoteRepository.findLatestOneByMemberId(memberId)?.let { quote ->
            signQuoteWithMemberId(quote, false, signedRequest, null)
        } ?: throw IllegalStateException("Tried to perform member sign with no quote!")
    }

    private fun signQuoteWithMemberId(
        quote: Quote,
        shouldCompleteSignInMemberService: Boolean,
        signedRequest: SignRequest,
        email: String?
    ): Either<Nothing, SignedQuoteResponseDto> {
        return Right(quote)
            .map { checkNotNull(it.memberId) { "Quote must have a member id! Quote id: ${it.id}" }; it }
            .map { checkNotNull(it.price) { "Quote must have a price to sign! Quote id: ${it.id}" }; it }
            .map { finalizeQuoteFromRapio(it, email) }
            .map { createContractsForQuote(it, signedRequest) }
            .flatMap {
                redeemAndSignQuoteAndPostToCustomerio(
                    it,
                    it.agreementId!!,
                    shouldCompleteSignInMemberService,
                    it.contractId!!
                )
            }
    }

    private fun createContractsForQuote(quote: Quote, signedRequest: SignRequest): Quote {
        val result =
            productPricingService.createContractsFromQuotes(listOf(quote), signedRequest, quote.signFromHopeTriggeredBy)
                .first()

        return quoteRepository.update(
            quote.copy(agreementId = result.agreementId, contractId = result.contractId)
        )
    }

    private fun finalizeQuoteFromRapio(quote: Quote, email: String?): Quote {
        if (quote.initiatedFrom == QuoteInitiatedFrom.RAPIO) {
            email?.let {
                memberService.finalizeOnboarding(quote, it)
            }
                ?: throw IllegalArgumentException("Must have an email when signing from rapio!")
        }
        return quote
    }

    companion object {
        val logger = LoggerFactory.getLogger(this.javaClass)!!
    }
}

private fun assertAgreementIdIsNotNull(quote: Quote): Quote {
    if (quote.agreementId != null) {
        throw RuntimeException("There is a signed product id ${quote.agreementId} already")
    }
    return quote
}

private fun updateStartTimeFromRequest(
    quote: Quote,
    body: SignQuoteRequest
): Quote {
    return when {
        body.startDate != null -> {
            quote.copy(
                startDate = body.startDate
            )
        }
        else -> quote.copy(startDate = null)
    }
}

private fun updateNameFromRequest(
    quote: Quote,
    body: SignQuoteRequest
): Quote {
    return if (body.name != null && quote.data is PersonPolicyHolder<*>) {
        quote.copy(data = quote.data.updateName(firstName = body.name.firstName, lastName = body.name.lastName))
    } else {
        quote
    }
}
