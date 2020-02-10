package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import java.time.LocalDate

object NorwegianPersonGuidelines {
    val setOfRules = setOf(
        NorwegianSecurityNumberFormat,
        NorwegianSocialSecurityDate,
        AgeRestrictionGuideline
    )
}

object NorwegianSecurityNumberFormat : BaseGuideline<QuoteData> {
    override val errorMessage = "SSN Invalid length"

    override val skipAfter: Boolean
        get() = true

    private fun getSSNLength(data: QuoteData): Int = TODO()

    override val validate = { data: QuoteData ->
        TODO()
    }
}

object NorwegianSocialSecurityDate : BaseGuideline<QuoteData> {
    override val errorMessage = "Invalid SSN"

    override val skipAfter: Boolean
        get() = true

    private fun getPossibleDateFromSSN(data: QuoteData): String {
        TODO()
    }

    private fun tryParse(input: String): Boolean {
        return try {
            LocalDate.parse(input) is LocalDate
        } catch (e: Exception) {
            false
        }
    }

    override val validate = { data: QuoteData ->
        !tryParse(getPossibleDateFromSSN(data))
    }
}
