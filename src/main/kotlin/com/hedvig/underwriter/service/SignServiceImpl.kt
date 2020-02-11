package com.hedvig.underwriter.service

import arrow.core.Either
import arrow.core.Right
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.QuoteRepository
import com.hedvig.underwriter.model.QuoteState
import com.hedvig.underwriter.service.exceptions.QuoteNotFoundException
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.serviceIntegration.customerio.CustomerIO
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.UpdateSsnRequest
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.RedeemCampaignDto
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.SignedQuoteRequest
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignRequest
import com.hedvig.underwriter.web.dtos.SignedQuoteResponseDto
import com.hedvig.underwriter.web.dtos.UnderwriterQuoteSignRequest
import feign.FeignException
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import org.javamoney.moneta.Money
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service

@Service
class SignServiceImpl(
    val quoteService: QuoteService,
    val quoteRepository: QuoteRepository,
    val memberService: MemberService,
    val productPricingService: ProductPricingService,
    val customerIO: CustomerIO,
    val env: Environment
) : SignService {
    override fun signQuote(
        completeQuoteId: UUID,
        body: SignQuoteRequest
    ): Either<ErrorResponseDto, SignedQuoteResponseDto> {
        val quote = quoteRepository.find(completeQuoteId)
            ?: throw QuoteNotFoundException("Quote $completeQuoteId not found when trying to sign")

        if (quote.signedProductId != null) {
            throw RuntimeException("There is a signed product id ${quote.signedProductId} already")
        }

        val updatedName = if (body.name != null && quote.data is PersonPolicyHolder<*>) {
            quote.copy(data = quote.data.updateName(firstName = body.name.firstName, lastName = body.name.lastName))
        } else {
            quote
        }

        val updatedStartTime = when {
            body.startDate != null -> {
                updatedName.copy(
                    startDate = body.startDate
                )
            }
            else -> updatedName.copy(startDate = null)
        }

        val quoteWithMember = if (quote.memberId == null) {
            val quoteNotSignableErrorDto = quoteService.getQuoteStateNotSignableErrorOrNull(quote)
            if (quoteNotSignableErrorDto != null) {
                return Either.left(quoteNotSignableErrorDto)
            }

            val memberAlreadySigned = when (quote.data) {
                is PersonPolicyHolder<*> -> memberService.isSsnAlreadySignedMemberEntity(quote.data.ssn!!)
                else -> throw RuntimeException("Unsupported quote data class")
            }

            if (memberAlreadySigned.ssnAlreadySignedMember) {
                return Either.Left(
                    ErrorResponseDto(
                        ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE,
                        "quote is already signed"
                    )
                )
            }

            val memberId = memberService.createMember()

            memberService.updateMemberSsn(memberId.toLong(), UpdateSsnRequest(ssn = quote.data.ssn!!))

            quoteRepository.update(updatedStartTime.copy(memberId = memberId))
        } else {
            quote
        }

        return Right(
            signQuoteWithMemberId(
                quoteWithMember,
                false,
                SignRequest("", "", ""),
                body.email
            )
        )
    }

    override fun memberSigned(memberId: String, signedRequest: SignRequest) {
        quoteRepository.findLatestOneByMemberId(memberId)?.let { quote ->
            signQuoteWithMemberId(quote, true, signedRequest, null)
        } ?: throw IllegalStateException("Tried to perform member sign with no quote!")
    }

    private fun signQuoteWithMemberId(
        quote: Quote,
        signedInMemberService: Boolean,
        signedRequest: SignRequest,
        email: String?
    ): SignedQuoteResponseDto {
        checkNotNull(quote.memberId) { "Quote must have a member id! Quote id: ${quote.id}" }
        checkNotNull(quote.price) { "Quote must have a price to sign! Quote id: ${quote.id}" }

        if (quote.initiatedFrom == QuoteInitiatedFrom.RAPIO) {
            email?.let {
                memberService.finalizeOnboarding(quote, it)
            }
                ?: throw IllegalArgumentException("Must have an email when signing from rapio!")
        }

        val signedProductId = productPricingService.signedQuote(
            SignedQuoteRequest(
                price = Money.of(quote.price, quote.currency),
                quote = quote,
                referenceToken = signedRequest.referenceToken,
                signature = signedRequest.signature,
                oscpResponse = signedRequest.oscpResponse
            ),
            quote.memberId
        ).id

        val quoteWithProductId = quoteRepository.update(quote.copy(signedProductId = signedProductId))
        checkNotNull(quoteWithProductId.memberId) { "Quote must have a member id! Quote id: ${quote.id}" }

        quoteWithProductId.attributedTo.campaignCode?.let { campaignCode ->
            try {
                productPricingService.redeemCampaign(
                    RedeemCampaignDto(
                        quoteWithProductId.memberId,
                        campaignCode,
                        LocalDate.now(ZoneId.of("Europe/Stockholm"))
                    )
                )
            } catch (e: FeignException) {
                logger.error("Failed to redeem $campaignCode for partner ${quoteWithProductId.attributedTo} with response ${e.message}")
            }
        }

        if (!signedInMemberService && quoteWithProductId.data is PersonPolicyHolder<*>) {
            memberService.signQuote(
                quoteWithProductId.memberId.toLong(),
                UnderwriterQuoteSignRequest(quoteWithProductId.data.ssn!!)
            )
        }

        val signedAt = Instant.now()
        val signedQuote = quoteWithProductId.copy(state = QuoteState.SIGNED)

        quoteRepository.update(signedQuote, signedAt)

        val activeProfiles = env.activeProfiles.intersect(listOf("staging", "production"))
        try {
            if (activeProfiles.isNotEmpty()) {
                logger.error("customerIOClient is null even thou $activeProfiles is set")
            }
            customerIO.postSignUpdate(quoteWithProductId)
        } catch (ex: Exception) {
            logger.error(
                "Something went wrong while posting a signing update to customerIO " +
                    "[ActiveProfile: $activeProfiles] [SignQuote: $signedQuote]"
            )
        }

        return SignedQuoteResponseDto(signedProductId, signedAt)
    }

    companion object {
        val logger = LoggerFactory.getLogger(this.javaClass)!!
    }
}
