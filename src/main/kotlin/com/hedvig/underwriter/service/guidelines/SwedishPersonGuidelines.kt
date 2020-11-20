package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.DEBT_CHECK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.INVALID_SSN
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.INVALID_SSN_LENGTH
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.SSN_DOES_NOT_MATCH_BIRTH_DATE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.UNDERAGE
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import java.time.LocalDate

class SwedishPersonalGuidelines(debtChecker: DebtChecker) {
    val setOfRules = setOf(
        SocialSecurityNumberFormat,
        SocialSecurityDate,
        AgeRestrictionGuideline,
        PersonalDebt(debtChecker),
        SocialSecurityNumberMatchesBirthDate
    )
}

object SocialSecurityNumberFormat : BaseGuideline<QuoteData> {
    override val breachedGuideline = BreachedGuideline(
        "SSN Invalid length",
        INVALID_SSN_LENGTH
    )

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

object SocialSecurityNumberMatchesBirthDate : BaseGuideline<QuoteData> {
    override val breachedGuideline = BreachedGuideline(
        "Birth date does not match SSN",
        SSN_DOES_NOT_MATCH_BIRTH_DATE
    )

    override val validate = { data: QuoteData ->
        !LocalDate.parse(getPossibleDateFromSSN(data)).isEqual((data as PersonPolicyHolder<*>).birthDate)
    }
}

object SocialSecurityDate : BaseGuideline<QuoteData> {
    override val breachedGuideline = BreachedGuideline(
        "Invalid SSN",
        INVALID_SSN
    )

    override
    val skipAfter: Boolean
        get() = true

    private fun tryParse(input: String): Boolean {
        return try {
            LocalDate.parse(input) is LocalDate
        } catch (e: Exception) {
            false
        }
    }

    override
    val validate = { data: QuoteData ->
        !tryParse(getPossibleDateFromSSN(data))
    }
}

object AgeRestrictionGuideline : BaseGuideline<QuoteData> {
    override val breachedGuideline = BreachedGuideline(
        "member is younger than 18",
        UNDERAGE
    )

    override val skipAfter: Boolean
        get() = true

    override val validate = { data: QuoteData -> (data as PersonPolicyHolder<*>).age() < 18 }
}

class PersonalDebt(val debtChecker: DebtChecker) : BaseGuideline<QuoteData> {
    override val breachedGuideline = BreachedGuideline(
        ERROR_MESSAGE,
        DEBT_CHECK
    )

    override val skipAfter: Boolean
        get() = true

    private fun debtCheck(data: QuoteData): List<String> =
        debtChecker.passesDebtCheck(data as PersonPolicyHolder<*>)

    override val validate = { data: QuoteData -> debtCheck(data).isNotEmpty() }

    companion object {
        const val ERROR_MESSAGE = "fails debt check"
    }
}

private fun getPossibleDateFromSSN(data: QuoteData): String {
    var trimmedInput = (data as PersonPolicyHolder<*>).ssn!!.trim()
    trimmedInput = trimmedInput.substring(0, 4) + "-" + trimmedInput.substring(
        4,
        6
    ) + "-" + trimmedInput.substring(6, 8)

    return trimmedInput
}
