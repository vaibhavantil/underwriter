package com.hedvig.underwriter.graphql

import arrow.core.Either
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.graphql.commons.extensions.getAcceptLanguage
import com.hedvig.graphql.commons.extensions.getTokenOrNull
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.service.LocalizationService
import com.hedvig.service.TextKeysLocaleResolver
import com.hedvig.underwriter.extensions.createCompleteQuoteResult
import com.hedvig.underwriter.extensions.toIncompleteQuoteDto
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.QuoteResult
import com.hedvig.underwriter.graphql.type.RemoveCurrentInsurerInput
import com.hedvig.underwriter.graphql.type.UnderwritingLimit
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.memberService.MemberService
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.EditMemberRequest.Companion.fromCreateQuoteinput
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.toEditMemberRequest
import com.hedvig.underwriter.web.dtos.ErrorCodes
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

        val quote = quoteService.createQuote(
            input.toIncompleteQuoteDto(memberId = env.getTokenOrNull()),
            input.id,
            initiatedFrom = when {
                env.isAndroid() -> QuoteInitiatedFrom.ANDROID
                env.isIOS() -> QuoteInitiatedFrom.IOS
                else -> QuoteInitiatedFrom.WEBONBOARDING
            }
        )

        return when (val errorOrQuote = quoteService.completeQuote(quote.id)) {
            is Either.Left -> {
                when (errorOrQuote.a.errorCode) {
                    ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> {
                        QuoteResult.UnderwritingLimitsHit(
                            errorOrQuote.a.breachedUnderwritingGuidelines?.map { breachedUnderwritingGuidelines ->
                                UnderwritingLimit(breachedUnderwritingGuidelines)
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
            }
            is Either.Right -> {
                val completeQuoteResponseDto = errorOrQuote.b

                env.getTokenOrNull()?.let { memberId ->
                    memberService.editMember(memberId.toLong(), fromCreateQuoteinput(input))
                }

                QuoteResult.CompleteQuote(
                    id = completeQuoteResponseDto.id,
                    firstName = input.firstName,
                    lastName = input.lastName,
                    currentInsurer = input.currentInsurer,
                    price = MonetaryAmountV2(
                        completeQuoteResponseDto.price.toPlainString(),
                        "SEK"
                    ),
                    details = input.createCompleteQuoteResult(
                        localizationService,
                        textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
                    ),
                    expiresAt = completeQuoteResponseDto.validTo
                )
            }
        }
    }

    fun editQuote(input: EditQuoteInput): QuoteResult {
        TODO()
    }

    fun removeCurrentInsurer(input: RemoveCurrentInsurerInput): QuoteResult {
        TODO()
    }

    fun DataFetchingEnvironment.isAndroid() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.getHeader("User-Agent")?.contains("Android", false)
            ?: false

    fun DataFetchingEnvironment.isIOS() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.getHeader("User-Agent")?.contains("iOS", false)
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
}
