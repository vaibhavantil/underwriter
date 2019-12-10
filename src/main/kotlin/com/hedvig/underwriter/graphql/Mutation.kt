package com.hedvig.underwriter.graphql

import arrow.core.Either
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.graphql.commons.extensions.getTokenOrNull
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.extensions.toIncompleteQuoteDto
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.graphql.type.RemoveCurrentInsurerInput
import com.hedvig.underwriter.graphql.type.UnderwritingLimit
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.context.GraphQLServletContext
import java.lang.IllegalStateException
import java.time.LocalDate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Mutation @Autowired constructor(
    private val quoteService: QuoteService,
    private val localizationService: LocalizationService,
    private val textKeysLocaleResolver: TextKeysLocaleResolver,
    private val memberService: MemberService
) : GraphQLMutationResolver {

    // Do to discrepancy between the graphql schema and how the graphql library is implemented
    // we can but should never return QuoteResult.IncompleteQuote
    fun createQuote(createQuoteInput: CreateQuoteInput, env: DataFetchingEnvironment): QuoteResult {
        val ssn = if (createQuoteInput.ssn.length == 10) {
            addCenturyToSSN(createQuoteInput.ssn)
        } else {
            createQuoteInput.ssn
        }
        val input = createQuoteInput.copy(ssn = ssn)

        val incompleteQuote = quoteService.createQuote(
            input.toIncompleteQuoteDto(memberId = env.getTokenOrNull()),
            input.id,
            initiatedFrom = when {
                env.isAndroid() -> QuoteInitiatedFrom.ANDROID
                env.isIOS() -> QuoteInitiatedFrom.IOS
                else -> QuoteInitiatedFrom.WEBONBOARDING
            }
        )

        return when (val errorOrQuote = quoteService.completeQuote(incompleteQuote.id)) {
            is Either.Left -> getQuoteResultFromError(errorOrQuote.a)
            is Either.Right -> {
                val completeQuoteResponseDto = errorOrQuote.b

                val quote = quoteService.getQuote(completeQuoteResponseDto.id)
                    ?: throw RuntimeException("Quote must not be null!")
                env.getTokenOrNull()?.let { memberId ->
                    // This should be removed when underwriter handles sign
                    memberService.finalizeOnboarding(quote.copy(memberId = memberId), "")
                }

                quote.getCompleteQuoteResult(env, localizationService, textKeysLocaleResolver)
            }
        }
    }

    fun editQuote(input: EditQuoteInput, env: DataFetchingEnvironment): QuoteResult =
        responseForEditedQuote(
            quoteService.updateQuote(input.toIncompleteQuoteDto(memberId = env.getTokenOrNull()), input.id),
            env
        )

    fun removeCurrentInsurer(input: RemoveCurrentInsurerInput, env: DataFetchingEnvironment) =
        responseForEditedQuote(
            quoteService.removeCurrentInsurerFromQuote(input.id),
            env
        )

    fun responseForEditedQuote(errorOrQuote: Either<ErrorResponseDto, Quote>, env: DataFetchingEnvironment) =
        when (errorOrQuote) {
            is Either.Left -> getQuoteResultFromError(errorOrQuote.a)
            is Either.Right -> {
                val quote = errorOrQuote.b

                if (quote.isComplete) {
                    quote.getCompleteQuoteResult(env, localizationService, textKeysLocaleResolver)
                } else {
                    quote.getIncompleteQuoteResult(env, localizationService, textKeysLocaleResolver)
                }
            }
        }

    fun getQuoteResultFromError(errorResponse: ErrorResponseDto) = when (errorResponse.errorCode) {
        ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> {
            QuoteResult.UnderwritingLimitsHit(
                errorResponse.breachedUnderwritingGuidelines?.map { description ->
                    UnderwritingLimit(description)
                } ?: throw IllegalStateException("Breached underwriting guidelines with no list")
            )
        }
        ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE ->
            throw IllegalStateException("Member has existing insurance")
        ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED ->
            throw IllegalStateException("Quote has expired")
        ErrorCodes.NO_SUCH_QUOTE ->
            throw IllegalStateException("No such quote")
        ErrorCodes.INVALID_STATE ->
            throw IllegalStateException("Invalid state")
        ErrorCodes.UNKNOWN_ERROR_CODE ->
            throw IllegalStateException("Unknown error code")
    }

    fun DataFetchingEnvironment.isAndroid() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.getHeader("User-Agent")?.contains(
            androidAppUserAgentRegex
        )
            ?: false

    fun DataFetchingEnvironment.isIOS() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.getHeader("User-Agent")?.contains(
            iOSAppUserAgentRegex
        )
            ?: false

    private fun addCenturyToSSN(ssn: String): String {
        val personalIdentityNumberYear = ssn.substring(0, 2).toInt()
        val breakPoint = LocalDate.now().minusYears(10).year.toString().substring(2, 4).toInt()

        return if (personalIdentityNumberYear > breakPoint) {
            "19$ssn"
        } else {
            "20$ssn"
        }
    }

    companion object {
        private val iOSAppUserAgentRegex = Regex("^com\\.hedvig.+iOS")
        private val androidAppUserAgentRegex = Regex("^com\\.hedvig.+Android")
    }
}
