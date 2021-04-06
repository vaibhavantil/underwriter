package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianTravelData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.NEGATIVE_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_OVERAGE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED

object NorwegianTravelGuidelines {
    val setOfRules = setOf(
        NorwegianTravelCoInsuredGuideline,
        NorwegianYouthTravelAgeNotMoreThan30Years
    )
}

object NorwegianTravelCoInsuredGuideline : BaseGuideline<NorwegianTravelData> {

    override fun validate(data: NorwegianTravelData): BreachedGuidelineCode? {
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

object NorwegianYouthTravelAgeNotMoreThan30Years : BaseGuideline<NorwegianTravelData> {

    override fun validate(data: NorwegianTravelData): BreachedGuidelineCode? {
        if (data.isYouth && data.age() > 30) {
            return YOUTH_OVERAGE
        }
        return OK
    }
}
