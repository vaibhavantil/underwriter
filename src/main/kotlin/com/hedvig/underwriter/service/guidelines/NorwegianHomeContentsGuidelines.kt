package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.NEGATIVE_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_OVERAGE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_MUCH_LIVING_SPACE

object NorwegianHomeContentsGuidelines {
    val setOfRules = setOf(
        NorwegianHomeContentCoInsuredGuideline,
        NorwegianHomeContentLivingSpaceGuideline,
        NorwegianYouthHomeContentsAgeNotMoreThan30Years
    )
}

object NorwegianHomeContentCoInsuredGuideline : BaseGuideline<NorwegianHomeContentsData> {

    override fun validate(data: NorwegianHomeContentsData): BreachedGuidelineCode {
        if (data.coInsured < 0) {
            return NEGATIVE_NUMBER_OF_CO_INSURED
        }

        if (!data.isYouth && data.coInsured > 5) {
            return TOO_HIGH_NUMBER_OF_CO_INSURED
        }

        if (data.isYouth && data.coInsured > 0) {
            return YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED
        }

        return OK
    }
}

object NorwegianHomeContentLivingSpaceGuideline : BaseGuideline<NorwegianHomeContentsData> {

    override fun validate(data: NorwegianHomeContentsData): BreachedGuidelineCode {
        if (data.livingSpace < 1) {
            return TOO_SMALL_LIVING_SPACE
        }

        if (!data.isYouth && data.livingSpace > 250) {
            return TOO_MUCH_LIVING_SPACE
        }

        if (data.isYouth && data.livingSpace > 50) {
            return YOUTH_TOO_MUCH_LIVING_SPACE
        }

        return OK
    }
}

object NorwegianYouthHomeContentsAgeNotMoreThan30Years : BaseGuideline<NorwegianHomeContentsData> {

    override fun validate(data: NorwegianHomeContentsData): BreachedGuidelineCode {
        if (data.isYouth && data.age() > 30) {
            return YOUTH_OVERAGE
        }
        return OK
    }
}
