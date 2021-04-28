package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.dayMonthAndTwoDigitYearFromDDMMYYSsn
import com.hedvig.underwriter.model.isValidDanishSsn
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.INVALID_SSN
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.SSN_DOES_NOT_MATCH_BIRTH_DATE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.UNDERAGE
import com.hedvig.underwriter.service.model.PersonPolicyHolder

object DanishPersonGuidelines {
    val setOfRules = setOf(
        DanishAgeRestrictionGuideline,
        DanishSsnIsValid,
        DanishSsnNotMatchesBirthDate
    )
}

object DanishAgeRestrictionGuideline : BaseGuideline<QuoteData> {

    override val skipAfter: Boolean
        get() = true

    override fun validate(data: QuoteData): BreachedGuidelineCode {
        if ((data as PersonPolicyHolder<*>).age() < 18) {
            return UNDERAGE
        }

        return OK
    }
}

object DanishSsnIsValid : BaseGuideline<QuoteData> {

    override fun validate(data: QuoteData): BreachedGuidelineCode {

        val ssn = (data as PersonPolicyHolder<*>).ssn

        if (ssn != null && !ssn.isValidDanishSsn()) {
            return INVALID_SSN
        }

        return OK
    }
}

object DanishSsnNotMatchesBirthDate : BaseGuideline<QuoteData> {

    override fun validate(data: QuoteData): BreachedGuidelineCode {

        val ssn = (data as PersonPolicyHolder<*>).ssn
        val birthdate = data.birthDate

        if (ssn == null || birthdate == null) {
            return OK
        }

        val valid = ssn.dayMonthAndTwoDigitYearFromDDMMYYSsn().let { dayMonthYear ->
            (birthdate.dayOfMonth == dayMonthYear.first.toInt()) &&
                (birthdate.monthValue == dayMonthYear.second.toInt()) &&
                (birthdate.year.toString().substring(2, 4) == dayMonthYear.third)
        }

        if (!valid) {
            return SSN_DOES_NOT_MATCH_BIRTH_DATE
        }

        return OK
    }
}
