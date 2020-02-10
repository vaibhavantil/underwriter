package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianHomeContentsData
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

object NorwegianSecurityNumberFormat : BaseGuideline<NorwegianHomeContentsData> {
    override val errorMessage = "SSN Invalid length"

    override val skipAfter: Boolean
        get() = true

    private fun getSSNLength(data: NorwegianHomeContentsData): Int =
        (data as PersonPolicyHolder<*>).ssn!!.trim().replace("-", "").replace(
            " ",
            ""
        ).length

    override val validate = { data: NorwegianHomeContentsData ->
        getSSNLength(data) != 11
    }
}

object NorwegianSocialSecurityDate : BaseGuideline<NorwegianHomeContentsData> {
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

    override val validate = { data: NorwegianHomeContentsData ->
        !tryParse(data.ssn.birthDateStringFromNorwegianSsn())
    }
}
