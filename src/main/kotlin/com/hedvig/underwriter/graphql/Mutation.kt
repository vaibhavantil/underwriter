package com.hedvig.underwriter.graphql

import arrow.core.Either
import com.coxautodev.graphql.tools.GraphQLMutationResolver
import com.hedvig.graphql.commons.extensions.getAcceptLanguage
import com.hedvig.graphql.commons.extensions.getToken
import com.hedvig.graphql.commons.extensions.getTokenOrNull
import com.hedvig.graphql.commons.type.MonetaryAmountV2
import com.hedvig.localization.service.TextKeysLocaleResolver
import com.hedvig.underwriter.extensions.isAndroid
import com.hedvig.underwriter.extensions.isIOS
import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.graphql.type.CreateQuoteResult
import com.hedvig.underwriter.graphql.type.EditQuoteInput
import com.hedvig.underwriter.graphql.type.InsuranceCost
import com.hedvig.underwriter.graphql.type.RemoveCurrentInsurerInput
import com.hedvig.underwriter.graphql.type.RemoveStartDateInput
import com.hedvig.underwriter.graphql.type.TypeMapper
import com.hedvig.underwriter.graphql.type.UnderwritingLimit
import com.hedvig.underwriter.graphql.type.UnderwritingLimitsHit
import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.QuoteService
import com.hedvig.underwriter.serviceIntegration.productPricing.ProductPricingService
import com.hedvig.underwriter.web.dtos.ErrorCodes
import com.hedvig.underwriter.web.dtos.ErrorResponseDto
import graphql.schema.DataFetchingEnvironment
import graphql.servlet.context.GraphQLServletContext
import java.math.BigDecimal
import java.time.LocalDate
import org.javamoney.moneta.Money
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Mutation @Autowired constructor(
    private val quoteService: QuoteService,
    private val productPricingService: ProductPricingService,
    private val textKeysLocaleResolver: TextKeysLocaleResolver,
    private val typeMapper: TypeMapper
) : GraphQLMutationResolver {

    // Do to discrepancy between the graphql schema and how the graphql library is implemented
    // we can but should never return QuoteResult.IncompleteQuote
    fun createQuote(createQuoteInput: CreateQuoteInput, env: DataFetchingEnvironment): CreateQuoteResult {
        val input = when {
            createQuoteInput.apartment != null || createQuoteInput.house != null ||
                createQuoteInput.swedishApartment != null || createQuoteInput.swedishHouse != null -> {
                val ssn = if (createQuoteInput.ssn!!.length == 10) {
                    addCenturyToSSN(createQuoteInput.ssn)
                } else {
                    createQuoteInput.ssn
                }
                createQuoteInput.copy(ssn = ssn)
            }
            else -> createQuoteInput
        }

        val completeQuote = quoteService.createQuote(
            input.toHouseOrApartmentIncompleteQuoteDto(memberId = env.getTokenOrNull()),
            input.id,
            initiatedFrom = when {
                env.isAndroid() -> QuoteInitiatedFrom.ANDROID
                env.isIOS() -> QuoteInitiatedFrom.IOS
                else -> QuoteInitiatedFrom.WEBONBOARDING
            },
            underwritingGuidelinesBypassedBy = null,
            updateMemberService = true
        )

        return when (completeQuote) {
            is Either.Left -> getQuoteResultFromError(completeQuote.a)
            is Either.Right -> {
                val completeQuoteResponseDto = completeQuote.b

                val quote = quoteService.getQuote(completeQuoteResponseDto.id)
                    ?: throw RuntimeException("Quote must not be null!")

                typeMapper.mapToCompleteQuoteResult(
                    quote,
                    productPricingService.calculateInsuranceCost(
                        Money.of(quote.price, quote.currency),
                        env.getToken()
                    ),
                    textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
                )
            }
        }
    }

    fun editQuote(input: EditQuoteInput, env: DataFetchingEnvironment): CreateQuoteResult =
        responseForEditedQuote(
            quoteService.updateQuote(
                input.toHouseOrApartmentIncompleteQuoteDto(memberId = env.getTokenOrNull()),
                input.id
            ),
            env
        )

    fun removeCurrentInsurer(input: RemoveCurrentInsurerInput, env: DataFetchingEnvironment): CreateQuoteResult =
        responseForEditedQuote(
            quoteService.removeCurrentInsurerFromQuote(input.id),
            env
        )

    fun removeStartDate(input: RemoveStartDateInput, env: DataFetchingEnvironment): CreateQuoteResult =
        responseForEditedQuote(
            quoteService.removeStartDateFromQuote(input.id),
            env
        )

    private fun responseForEditedQuote(errorOrQuote: Either<ErrorResponseDto, Quote>, env: DataFetchingEnvironment): CreateQuoteResult =
        when (errorOrQuote) {
            is Either.Left -> getQuoteResultFromError(errorOrQuote.a)
            is Either.Right -> {
                val quote = errorOrQuote.b

                typeMapper.mapToCompleteQuoteResult(
                    quote,
                    when (quote.data) {
                        is SwedishHouseData,
                        is SwedishApartmentData -> productPricingService.calculateInsuranceCost(
                            Money.of(quote.price, "SEK"), env.getToken()
                        )
                        is NorwegianHomeContentsData,
                        is NorwegianTravelData -> InsuranceCost(
                            monthlyGross = MonetaryAmountV2.Companion.of(quote.price!!, "NOK"),
                            monthlyDiscount = MonetaryAmountV2.Companion.of(BigDecimal.ZERO, "NOK"),
                            monthlyNet = MonetaryAmountV2.Companion.of(quote.price, "NOK"),
                            freeUntil = null
                        )
                    },
                    textKeysLocaleResolver.resolveLocale(env.getAcceptLanguage())
                )
            }
        }

    private fun getQuoteResultFromError(errorResponse: ErrorResponseDto) = when (errorResponse.errorCode) {
        ErrorCodes.MEMBER_BREACHES_UW_GUIDELINES -> {
            UnderwritingLimitsHit(
                errorResponse.breachedUnderwritingGuidelines?.map { description ->
                    UnderwritingLimit(description)
                } ?: throw IllegalStateException("Breached underwriting guidelines with no list")
            )
        }
        ErrorCodes.MEMBER_HAS_EXISTING_INSURANCE ->
            throw IllegalStateException("Member has existing insurance [Error Message: ${errorResponse.errorMessage}]")
        ErrorCodes.MEMBER_QUOTE_HAS_EXPIRED ->
            throw IllegalStateException("Quote has expired [Error Message: ${errorResponse.errorMessage}]")
        ErrorCodes.NO_SUCH_QUOTE ->
            throw IllegalStateException("No such quote [Error Message: ${errorResponse.errorMessage}]")
        ErrorCodes.INVALID_STATE ->
            throw IllegalStateException("Invalid state [Error Message: ${errorResponse.errorMessage}]")
        ErrorCodes.UNKNOWN_ERROR_CODE ->
            throw IllegalStateException("Unknown error code [Error Message: ${errorResponse.errorMessage}]")
    }

    fun DataFetchingEnvironment.isAndroid() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.isAndroid() ?: false
        ?: false

    fun DataFetchingEnvironment.isIOS() =
        this.getContext<GraphQLServletContext?>()?.httpServletRequest?.isIOS() ?: false

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
