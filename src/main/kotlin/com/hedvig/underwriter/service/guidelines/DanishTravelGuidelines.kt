package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.DanishTravelData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.NEGATIVE_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.STUDENT_OVERAGE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_CO_INSURED

object DanishTravelGuidelines {
    val setOfRules = setOf(
        DanishTravelCoInsuredGuideline,
        DanishTravelStudentAgeGuideline
    )
}

object DanishTravelCoInsuredGuideline : BaseGuideline<DanishTravelData> {
    override fun validate(data: DanishTravelData): BreachedGuidelineCode? {
        if (data.coInsured < 0) {
            return NEGATIVE_NUMBER_OF_CO_INSURED
        }

        if (data.isStudent && data.coInsured > 1) {
            return TOO_HIGH_NUMBER_OF_CO_INSURED
        }

        if (!data.isStudent && data.coInsured > 6) {
            return TOO_HIGH_NUMBER_OF_CO_INSURED
        }

        return OK
    }
}

object DanishTravelStudentAgeGuideline : BaseGuideline<DanishTravelData> {

    override fun validate(data: DanishTravelData): BreachedGuidelineCode? {

        if (data.isStudent && data.age() !in 18..30) {
            return STUDENT_OVERAGE
        }

        return OK
    }
}
