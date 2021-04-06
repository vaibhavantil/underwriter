package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.DanishHomeContentsData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.NEGATIVE_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.STUDENT_OVERAGE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.STUDENT_TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_LIVING_SPACE

object DanishHomeContentsGuidelines {
    val setOfRules = setOf(
        DanishHomeContentCoInsuredGuideline,
        DanishHomeContentLivingSpaceGuideline,
        DanisHomeContentStudentAgeGuideline
    )
}

object DanishHomeContentCoInsuredGuideline : BaseGuideline<DanishHomeContentsData> {
    override fun validate(data: DanishHomeContentsData): BreachedGuidelineCode? {
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

object DanishHomeContentLivingSpaceGuideline : BaseGuideline<DanishHomeContentsData> {
    override fun validate(data: DanishHomeContentsData): BreachedGuidelineCode? {
        if (data.livingSpace < 5) {
            return TOO_SMALL_LIVING_SPACE
        }

        if (data.isStudent) {
            if (data.livingSpace > 100) {
                return STUDENT_TOO_MUCH_LIVING_SPACE
            }
        } else {
            if (data.livingSpace > 250) {
                return TOO_MUCH_LIVING_SPACE
            }
        }

        return OK
    }
}

object DanisHomeContentStudentAgeGuideline : BaseGuideline<DanishHomeContentsData> {

    override fun validate(data: DanishHomeContentsData): BreachedGuidelineCode? {

        if (data.isStudent && data.age() !in 18..30) {
            return STUDENT_OVERAGE
        }

        return OK
    }
}
