package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.NEGATIVE_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_OVERAGE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_MUCH_LIVING_SPACE
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object NorwegianHomeContentsGuidelines {
    val setOfRules = setOf(
        NorwegianHomeContentcoInsuredCantBeNegative,
        NorwegianHomeContentLivingSpaceAtLeast1Sqm,
        NorwegianHomeContentscoInsuredNotMoreThan5,
        NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm,
        NorwegianYouthHomeContentsLivingSpaceNotMoreThan50Sqm,
        NorwegianYouthHomeContentsAgeNotMoreThan30Years,
        NorwegianYouthHomeContentsCoInsuredNotMoreThan0
    )
}

object NorwegianHomeContentcoInsuredCantBeNegative : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = NEGATIVE_NUMBER_OF_CO_INSURED

    override val validate = { data: NorwegianHomeContentsData -> data.coInsured < 0 }
}

object NorwegianHomeContentLivingSpaceAtLeast1Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = TOO_SMALL_LIVING_SPACE

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace < 1 }
}

object NorwegianHomeContentscoInsuredNotMoreThan5 : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = TOO_HIGH_NUMBER_OF_CO_INSURED

    override val validate = { data: NorwegianHomeContentsData -> data.coInsured > 5 }
}

object NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = TOO_MUCH_LIVING_SPACE

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace > 250 }
}

object NorwegianYouthHomeContentsCoInsuredNotMoreThan0 : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED

    override val validate =
        { data: NorwegianHomeContentsData ->
            (data.isYouth) &&
                data.coInsured > 0
        }
}

object NorwegianYouthHomeContentsLivingSpaceNotMoreThan50Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = YOUTH_TOO_MUCH_LIVING_SPACE

    override val validate =
        { data: NorwegianHomeContentsData ->
            (data.isYouth) &&
                data.livingSpace > 50
        }
}

object NorwegianYouthHomeContentsAgeNotMoreThan30Years : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = YOUTH_OVERAGE

    override val validate =
        { data: NorwegianHomeContentsData ->
            (data.isYouth) &&
                data.birthDate.until(LocalDate.now(), ChronoUnit.YEARS) > 30
        }
}
