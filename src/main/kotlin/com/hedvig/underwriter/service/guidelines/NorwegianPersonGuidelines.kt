package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.dayMonthAndTwoDigitYearFromDDMMYYSsn
import com.hedvig.underwriter.model.isValidNorwegianSsn
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.INVALID_SSN
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.SSN_DOES_NOT_MATCH_BIRTH_DATE
import com.hedvig.underwriter.service.model.PersonPolicyHolder

object NorwegianPersonGuidelines {
    val setOfRules = setOf(
        AgeRestrictionGuideline,
        NorwegianSSnIsValid,
        NorwegianSsnNotMatchesBirthDate
    )
}

private val ssnDoesNotMatchBirthDate = BreachedGuideline(
    "breaches underwriting guidelines ssn does not match birth date",
    SSN_DOES_NOT_MATCH_BIRTH_DATE
)

private val invalidSsn = BreachedGuideline(
    "breaches underwriting guidelines ssn is not valid",
    INVALID_SSN
)

object NorwegianSsnNotMatchesBirthDate : BaseGuideline<QuoteData> {
    override val breachedGuideline = ssnDoesNotMatchBirthDate

    override val validate =
        { data: QuoteData ->
            (data as PersonPolicyHolder<*>).ssn?.dayMonthAndTwoDigitYearFromDDMMYYSsn()?.let { dayMonthYear ->
                !(
                    (data.birthDate!!.dayOfMonth == dayMonthYear.first.toInt()) &&
                        (data.birthDate!!.monthValue == dayMonthYear.second.toInt()) &&
                        (data.birthDate!!.year.toString().substring(2, 4) == dayMonthYear.third)
                    )
            } ?: false
        }
}

object NorwegianSSnIsValid : BaseGuideline<QuoteData> {
    override val breachedGuideline = invalidSsn

    override val validate = { data: QuoteData ->
        (data as PersonPolicyHolder<*>).ssn?.let { !it.isValidNorwegianSsn() } ?: false
    }
}
