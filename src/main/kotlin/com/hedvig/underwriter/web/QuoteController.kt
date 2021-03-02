package com.hedvig.underwriter.web

import arrow.core.Either
import arrow.core.getOrHandle
import com.hedvig.graphql.commons.extensions.isAndroid
import com.hedvig.graphql.commons.extensions.isIOS
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.BundleQuotesService
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.SignService
import com.hedvig.underwriter.service.model.QuoteRequest
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuoteDto
import com.hedvig.underwriter.util.logger
import com.hedvig.underwriter.util.toMaskedString
import com.hedvig.underwriter.web.dtos.AddAgreementFromQuoteRequest
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorQuoteResponseDto
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import com.hedvig.underwriter.web.dtos.MarketInfo
import com.hedvig.underwriter.web.dtos.QuoteBundleRequestDto
import com.hedvig.underwriter.web.dtos.QuoteBundleResponseDto
import com.hedvig.underwriter.web.dtos.QuoteForNewContractRequestDto
import com.hedvig.underwriter.web.dtos.QuoteRequestDto
import com.hedvig.underwriter.web.dtos.QuoteRequestFromAgreementDto
import com.hedvig.underwriter.web.dtos.SignQuoteFromHopeRequest
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.Email

@RestController
@RequestMapping(
    "/_/v1/quote", // Deprecated
    "/_/v1/quotes"
)
class QuoteController @Autowired constructor(
    val quoteService: QuoteService,
    val memberService: MemberService,
    val signService: SignService,
    val bundleQuotesService: BundleQuotesService
) {
    @PostMapping
    fun createQuote(
        @Valid @RequestBody requestDto: QuoteRequestDto,
        httpServletRequest: HttpServletRequest
    ): ResponseEntity<out Any> {

        logger.info("Create quote. Request: ${requestDto.toMaskedString()}")

        val houseOrApartmentIncompleteQuoteDto = QuoteRequest.from(requestDto)

        val quoteInitiatedFrom = when {
            httpServletRequest.isAndroid() -> QuoteInitiatedFrom.ANDROID
            httpServletRequest.isIOS() -> QuoteInitiatedFrom.IOS
            requestDto.originatingProductId != null -> QuoteInitiatedFrom.HOPE
            else -> QuoteInitiatedFrom.RAPIO
        }

        logger.info("Initiated from $quoteInitiatedFrom")

        return quoteService.createQuote(
            houseOrApartmentIncompleteQuoteDto,
            initiatedFrom = quoteInitiatedFrom,
            underwritingGuidelinesBypassedBy = requestDto.underwritingGuidelinesBypassedBy,
            updateMemberService = false
        )
            .bimap(
                { ResponseEntity.status(422).body(it) },
                { ResponseEntity.status(200).body(it) }
            ).getOrHandle { it }
    }

    @PostMapping("/createQuoteFromAgreement")
    fun createQuoteFromAgreement(
        @RequestBody quoteRequest: QuoteRequestFromAgreementDto
    ): ResponseEntity<out Any> {

        logger.info("Create quote from agreement. Request: ${quoteRequest.toMaskedString()}")

        return quoteService.createQuoteFromAgreement(
            agreementId = quoteRequest.agreementId,
            memberId = quoteRequest.memberId,
            underwritingGuidelinesBypassedBy = quoteRequest.underwritingGuidelinesBypassedBy
        ).bimap(
            { ResponseEntity.status(422).body(it) },
            { ResponseEntity.status(200).body(it) }
        ).getOrHandle { it }
    }

    @PostMapping("/createQuoteForNewContract")
    fun createQuoteFromAgreement(
        @RequestBody request: QuoteForNewContractRequestDto
    ): ResponseEntity<out Any> {

        logger.info("Create quote from contract. Request: ${request.toMaskedString()}")

        return quoteService.createQuoteForNewContractFromHope(
            quoteRequest = QuoteRequest.from(request.quoteRequestDto),
            underwritingGuidelinesBypassedBy = request.underwritingGuidelinesBypassedBy
        ).bimap(
            { ResponseEntity.status(422).body(it) },
            { ResponseEntity.status(200).body(it) }
        ).getOrHandle { it }
    }

    @PostMapping(
        path = [
            "/{incompleteQuoteId}/completeQuote",
            "/{incompleteQuoteId}/complete"
        ]
    )
    fun completeQuote(
        @Valid @PathVariable incompleteQuoteId: UUID,
        @Valid
        @Email
        @RequestParam("underwritingGuidelinesBypassedBy")
        underwritingGuidelinesBypassedBy: String?
    ): ResponseEntity<Any> {
        logger.error("completeQuote endpoint was used. incompleteQuoteId: $incompleteQuoteId underwritingGuidelinesBypassedBy: $underwritingGuidelinesBypassedBy")
        return ResponseEntity.status(HttpStatus.GONE)
            .body(ErrorResponseDto(ErrorCodes.UNKNOWN_ERROR_CODE, "endpoint is deprecated"))
    }

    @GetMapping("/{id}")
    fun getQuote(@PathVariable id: UUID): ResponseEntity<Quote> {
        logger.info("Get quote for quoteId=$id")

        val optionalQuote = quoteService.getQuote(id) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(optionalQuote)
    }

    @PatchMapping("/{id}")
    fun updateQuoteInfo(
        @PathVariable id: UUID,
        @RequestBody @Valid quoteRequestDto: QuoteRequestDto,
        @Valid
        @Email
        @RequestParam("underwritingGuidelinesBypassedBy")
        underwritingGuidelinesBypassedBy: String?
    ): ResponseEntity<Any> {
        logger.info("Update quote. quoteId=$id, request: ${quoteRequestDto.toMaskedString()}, underwritingGuidelinesBypassedBy=$underwritingGuidelinesBypassedBy")

        val houseOrApartmentIncompleteQuoteDto = QuoteRequest.from(quoteRequestDto)

        return when (val quoteOrError =
            quoteService.updateQuote(houseOrApartmentIncompleteQuoteDto, id, underwritingGuidelinesBypassedBy)) {
            is Either.Left -> ResponseEntity.status(422).body(quoteOrError.a)
            is Either.Right -> ResponseEntity.status(200).body(QuoteDto.fromQuote(quoteOrError.b))
        }
    }

    @PostMapping("/bundle")
    fun quoteBundle(@RequestBody @Valid request: QuoteBundleRequestDto): QuoteBundleResponseDto {

        val cost = bundleQuotesService.bundleQuotes(
            memberId = null,
            ids = request.quoteIds
        )

        return QuoteBundleResponseDto.from(cost)
    }

    @PostMapping("/{completeQuoteId}/sign")
    fun signCompleteQuote(
        @Valid @PathVariable completeQuoteId: UUID,
        @RequestBody body: SignQuoteRequest
    ): ResponseEntity<Any> {
        logger.info("Sign quote. Request: ${body.toMaskedString()}, completeQuoteId=$completeQuoteId")

        return when (val errorOrQuote = signService.signQuote(completeQuoteId, body)) {
            is Either.Left -> ResponseEntity.status(422).body(errorOrQuote.a)
            is Either.Right -> ResponseEntity.status(200).body(errorOrQuote.b)
        }
    }

    @PostMapping("/{completeQuoteId}/signFromHope")
    fun signQuoteFromHope(
        @Valid @PathVariable completeQuoteId: UUID,
        @RequestBody request: SignQuoteFromHopeRequest
    ): ResponseEntity<Any> {
        logger.info("Sign quote from Hope. Request: ${request.toMaskedString()}, completeQuoteId=$completeQuoteId")

        return when (val errorOrQuote = signService.signQuoteFromHope(completeQuoteId, request)) {
            is Either.Left -> ResponseEntity.status(422).body(errorOrQuote.a)
            is Either.Right -> ResponseEntity.status(200).body(errorOrQuote.b)
        }
    }

    @PostMapping("/add/agreement")
    fun addAgreementToContractTimeline(
        @Valid @RequestBody request: AddAgreementFromQuoteRequest,
        @RequestHeader("Authorization") token: String?
    ): ResponseEntity<Any> {
        logger.info("Add agreement to contract. Request: ${request.toMaskedString()}")

        val result = quoteService.addAgreementFromQuote(request, token)

        return when (result) {
            is Either.Left -> ResponseEntity.status(422).body(result.a)
            is Either.Right -> ResponseEntity.ok(result.b)
            else -> throw IllegalStateException("Result should be either left or right but was ${result::class.java}")
        }
    }

    @GetMapping("/members/{memberId}/latestQuote")
    fun getLatestQuoteFromMemberId(@PathVariable memberId: String): ResponseEntity<QuoteDto> {
        logger.info("Get last quote for member: memberId=$memberId")

        val quote = quoteService.getLatestQuoteForMemberId(memberId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(QuoteDto.fromQuote(quote))
    }

    @GetMapping("/members/{memberId}")
    fun getAllQuotesFromMemberId(@PathVariable memberId: String): ResponseEntity<List<QuoteDto>> {
        logger.info("Get all quotes for member: memberId=$memberId")

        return ResponseEntity.ok(quoteService.getQuotesForMemberId(memberId))
    }

    @Deprecated("Should start sign session from `/_/v1/signSession` and complete it there")
    @PostMapping("/member/{memberId}/signed")
    fun memberSigned(@PathVariable memberId: String, @RequestBody signRequest: SignRequest): ResponseEntity<Void> {
        logger.info("Member signed (deprecated): memberId=$memberId")

        signService.memberSigned(memberId, signRequest)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/expire")
    fun expireInvalidQuotes(@PathVariable id: UUID): ResponseEntity<Quote> {
        logger.info("Expire invalid quote: quoteId=$id")

        val quote = quoteService.expireQuote(id) ?: return ResponseEntity.noContent().build()
        return ResponseEntity.ok(quote)
    }

    @GetMapping("/contracts/{contractId}")
    fun getContractById(@PathVariable contractId: UUID): ResponseEntity<Any> {
        logger.info("Get contract: contractId=$contractId")
        val quote = quoteService.getQuoteByContractId(contractId = contractId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ErrorQuoteResponseDto(
                    errorCode = ErrorCodes.NO_SUCH_QUOTE,
                    errorMessage = "QuoteNotFound"
                )
            )

        return ResponseEntity.ok(QuoteDto.fromQuote(quote))
    }

    @GetMapping("/members/{memberId}/latestQuote/marketInfo")
    fun getMarketInfoFromLatestQuote(@PathVariable memberId: String): ResponseEntity<MarketInfo> {
        logger.info("Get market from last quote for member: memberId=$memberId")
        val market = quoteService.getMarketFromLatestQuote(memberId)
        return ResponseEntity.ok(MarketInfo(market = market))
    }
}
