package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.DebtChecker
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.DEBT_CHECK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.INVALID_SSN
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.INVALID_SSN_LENGTH
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.SSN_DOES_NOT_MATCH_BIRTH_DATE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.UNDERAGE
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import java.time.LocalDate

class SwedishPersonalGuidelines(debtChecker: DebtChecker) {
    val setOfRules = setOf(
        SocialSecurityNumberFormat,
        SocialSecurityDate,
        SwedishAgeRestrictionGuideline,
        SwedishPersonalDebt(debtChecker),
        SocialSecurityNumberMatchesBirthDate
    )
}

object SocialSecurityNumberFormat : BaseGuideline<QuoteData> {

    override val skipAfter: Boolean
        get() = true

    private fun getSSNLength(data: QuoteData): Int =
        (data as PersonPolicyHolder<*>).ssn!!.trim().replace("-", "").replace(
            " ",
            ""
        ).length

    override fun validate(data: QuoteData): BreachedGuidelineCode? {
        if (getSSNLength(data) != 12) {
            return INVALID_SSN_LENGTH
        }
        return OK
    }
}

object SocialSecurityNumberMatchesBirthDate : BaseGuideline<QuoteData> {

    override fun validate(data: QuoteData): BreachedGuidelineCode? {

        val birthdate = (data as PersonPolicyHolder<*>).birthDate
        val ssnBirthdate = LocalDate.parse(getPossibleDateFromSSN(data))

        if (ssnBirthdate != birthdate) {
            return SSN_DOES_NOT_MATCH_BIRTH_DATE
        }
        return OK
    }
}

object SocialSecurityDate : BaseGuideline<QuoteData> {

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

    override fun validate(data: QuoteData): BreachedGuidelineCode? {
        if (!tryParse(getPossibleDateFromSSN(data))) {
            return INVALID_SSN
        }
        return OK
    }
}

object SwedishAgeRestrictionGuideline : BaseGuideline<QuoteData> {
    override val skipAfter: Boolean
        get() = true

    override fun validate(data: QuoteData): BreachedGuidelineCode? {
        if ((data as PersonPolicyHolder<*>).age() < 18) {
            return UNDERAGE
        }
        return OK
    }
}

class SwedishPersonalDebt(private val debtChecker: DebtChecker) : BaseGuideline<QuoteData> {

    override val skipAfter: Boolean
        get() = true

    private fun debtCheck(data: QuoteData): List<String> =
        debtChecker.passesDebtCheck(data as PersonPolicyHolder<*>)

    override fun validate(data: QuoteData): BreachedGuidelineCode? {
        if (debtCheck(data).isNotEmpty()) {
            return DEBT_CHECK
        }
        return OK
    }
}

private fun getPossibleDateFromSSN(data: QuoteData): String {
    var trimmedInput = (data as PersonPolicyHolder<*>).ssn!!.trim()
    trimmedInput = trimmedInput.substring(0, 4) + "-" +
        trimmedInput.substring(4, 6) + "-" +
        trimmedInput.substring(6, 8)

    return trimmedInput
}
