package com.hedvig.underwriter.web

import arrow.core.Either
import arrow.core.getOrHandle
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.service.dtos.HouseOrApartmentIncompleteQuoteDto
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.productPricing.dtos.QuoteDto
import com.hedvig.underwriter.web.dtos.ActivateQuoteRequestDto
import com.hedvig.underwriter.web.dtos.QuoteRequestDto
import com.hedvig.underwriter.web.dtos.SignQuoteRequest
import com.hedvig.underwriter.web.dtos.SignRequest
import java.util.UUID
import javax.validation.Valid
import javax.validation.constraints.Email
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(
    "/_/v1/quote", // Deprecated
    "/_/v1/quotes"
)
class QuoteController @Autowired constructor(
    val quoteService: QuoteService,
    val memberService: MemberService
) {
    @PostMapping
    fun createQuote(@Valid @RequestBody requestDto: QuoteRequestDto): ResponseEntity<out Any> {
        val houseOrApartmentIncompleteQuoteDto = HouseOrApartmentIncompleteQuoteDto.from(requestDto)

        return quoteService.createQuote(
            houseOrApartmentIncompleteQuoteDto,
            shouldComplete = requestDto.shouldComplete,
            underwritingGuidelinesBypassedBy = requestDto.underwritingGuidelinesBypassedBy)
            .bimap(
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

        return when (val quoteOrError =
            quoteService.completeQuote(incompleteQuoteId, underwritingGuidelinesBypassedBy)) {
            is Either.Left -> ResponseEntity.status(422).body(quoteOrError.a)
            is Either.Right -> ResponseEntity.status(200).body(quoteOrError.b)
        }
    }

    @GetMapping("/{id}")
    fun getQuote(@PathVariable id: UUID): ResponseEntity<Quote> {
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
        val houseOrApartmentIncompleteQuoteDto = HouseOrApartmentIncompleteQuoteDto.from(quoteRequestDto)

        return when (val quoteOrError =
            quoteService.updateQuote(houseOrApartmentIncompleteQuoteDto, id, underwritingGuidelinesBypassedBy)) {
            is Either.Left -> ResponseEntity.status(422).body(quoteOrError.a)
            is Either.Right -> ResponseEntity.status(200).body(QuoteDto.fromQuote(quoteOrError.b))
        }
    }

    @PostMapping("/{completeQuoteId}/sign")
    fun signCompleteQuote(@Valid @PathVariable completeQuoteId: UUID, @RequestBody body: SignQuoteRequest): ResponseEntity<Any> {
        return when (val errorOrQuote = quoteService.signQuote(completeQuoteId, body)) {
            is Either.Left -> ResponseEntity.status(422).body(errorOrQuote.a)
            is Either.Right -> ResponseEntity.status(200).body(errorOrQuote.b)
        }
    }

    @PostMapping("/{completeQuoteId}/activate")
    fun activateCompleteQuote(
        @PathVariable completeQuoteId: UUID,
        @Valid @RequestBody requestBody: ActivateQuoteRequestDto
    ): ResponseEntity<Any> {
        val result =
            quoteService.activateQuote(completeQuoteId, requestBody.activationDate, requestBody.terminationDate)

        return when (result) {
            is Either.Left -> ResponseEntity.status(422).body(result.a)
            is Either.Right -> ResponseEntity.ok(result.b)
            else -> throw IllegalStateException("Result should be either left or right but was ${result::class.java}")
        }
    }

    @GetMapping("/members/{memberId}/latestQuote")
    fun getLatestQuoteFromMemberId(@PathVariable memberId: String): ResponseEntity<QuoteDto> {
        val quote = quoteService.getLatestQuoteForMemberId(memberId) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(QuoteDto.fromQuote(quote))
    }

    @GetMapping("/members/{memberId}")
    fun getAllQuotesFromMemberId(@PathVariable memberId: String): ResponseEntity<List<QuoteDto>> {
        return ResponseEntity.ok(quoteService.getQuotesForMemberId(memberId))
    }

    @PostMapping("/member/{memberId}/signed")
    fun memberSigned(@PathVariable memberId: String, @RequestBody signRequest: SignRequest): ResponseEntity.HeadersBuilder<*> {
        quoteService.memberSigned(memberId, signRequest)
        return ResponseEntity.noContent()
    }
}
