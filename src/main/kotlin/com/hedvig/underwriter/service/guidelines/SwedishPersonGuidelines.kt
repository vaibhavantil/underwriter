package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import java.time.LocalDate

class SwedishPersonalGuidelines(debtChecker: DebtChecker) {
    val setOfRules = setOf(
        SocialSecurityNumberFormat,
        SocialSecurityDate,
        AgeRestrictionGuideline,
        PersonalDebt(debtChecker)
    )
}

object SocialSecurityNumberFormat : BaseGuideline<QuoteData> {
    override val errorMessage = "SSN Invalid length"

    override val skipAfter: Boolean
        get() = true

    private fun getSSNLength(data: QuoteData): Int =
        (data as PersonPolicyHolder<*>).ssn!!.trim().replace("-", "").replace(
            " ",
            ""
        ).length

    override val validate = { data: QuoteData ->
        getSSNLength(data) != 12
    }
}

object SocialSecurityDate : BaseGuideline<QuoteData> {
    override val errorMessage = "Invalid SSN"

    override val skipAfter: Boolean
        get() = true

    private fun getPossibleDateFromSSN(data: QuoteData): String {
        var trimmedInput = (data as PersonPolicyHolder<*>).ssn!!.trim()
        trimmedInput = trimmedInput.substring(0, 4) + "-" + trimmedInput.substring(
            4,
            6
        ) + "-" + trimmedInput.substring(6, 8)

        return trimmedInput
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

object AgeRestrictionGuideline : BaseGuideline<QuoteData> {
    override val errorMessage = "member is younger than 18"

    override val skipAfter: Boolean
        get() = true

    override val validate = { data: QuoteData -> (data as PersonPolicyHolder<*>).age() < 18 }
}

class PersonalDebt(val debtChecker: DebtChecker) : BaseGuideline<QuoteData> {
    override val errorMessage = "fails debt check"

    override val skipAfter: Boolean
        get() = true

    private fun debtCheck(data: QuoteData): List<String> = debtChecker.passesDebtCheck(data as PersonPolicyHolder<*>)

    override val validate = { data: QuoteData -> debtCheck(data).isNotEmpty() }
}
