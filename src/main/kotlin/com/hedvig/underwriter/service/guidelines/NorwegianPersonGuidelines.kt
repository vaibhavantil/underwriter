package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.model.birthDateStringFromNorwegianSsn
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

    private fun getSSNLength(data: QuoteData): Int =
        (data as PersonPolicyHolder<*>).ssn!!.trim().replace("-", "").replace(
            " ",
            ""
        ).length

    override val validate = { data: QuoteData ->
        getSSNLength(data) != 11
    }
}

object NorwegianSocialSecurityDate : BaseGuideline<QuoteData> {
    override val errorMessage = "Invalid SSN"

    override val skipAfter: Boolean
        get() = true

    private fun tryParse(input: String): Boolean {
        return try {
            LocalDate.parse(input) is LocalDate
        } catch (e: Exception) {
            false
        }
    }

    override val validate = { data: QuoteData ->
        !tryParse((data as PersonPolicyHolder<*>).ssn!!.birthDateStringFromNorwegianSsn())
    }
}
