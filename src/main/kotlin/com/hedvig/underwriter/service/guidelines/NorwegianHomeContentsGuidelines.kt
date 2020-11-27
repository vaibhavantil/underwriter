package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianHomeContentsData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.NEGATIVE_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.YOUTH_OVERAGE
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
    override val breachedGuideline = BreachedGuideline("coInsured cant be negative", NEGATIVE_NUMBER_OF_CO_INSURED)

    override val validate = { data: NorwegianHomeContentsData -> data.coInsured < 0 }
}

object NorwegianHomeContentLivingSpaceAtLeast1Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = BreachedGuideline("living space must be at least 1 sqm", TOO_SMALL_LIVING_SPACE)

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace < 1 }
}

object NorwegianHomeContentscoInsuredNotMoreThan5 : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = BreachedGuideline("coInsured size must be less than or equal to 5", TOO_HIGH_NUMBER_OF_CO_INSURED)

    override val validate = { data: NorwegianHomeContentsData -> data.coInsured > 5 }
}

object NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = BreachedGuideline(
        "living space must be less than or equal to 250 sqm",
        TOO_MUCH_LIVING_SPACE
    )

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace > 250 }
}

object NorwegianYouthHomeContentsCoInsuredNotMoreThan0 : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = BreachedGuideline(
        "coInsured size must be less than or equal to 0",
        YOUTH_TOO_HIGH_NUMBER_OF_CO_INSURED
    )

    override val validate =
        { data: NorwegianHomeContentsData ->
            (data.isYouth) &&
                data.coInsured > 0
        }
}

object NorwegianYouthHomeContentsLivingSpaceNotMoreThan50Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = BreachedGuideline(
        "breaches underwriting guideline living space must be less than or equal to 50sqm",
        YOUTH_TOO_MUCH_LIVING_SPACE
    )

    override val validate =
        { data: NorwegianHomeContentsData ->
            (data.isYouth) &&
                data.livingSpace > 50
        }
}

object NorwegianYouthHomeContentsAgeNotMoreThan30Years : BaseGuideline<NorwegianHomeContentsData> {
    override val breachedGuideline = BreachedGuideline(
        "breaches underwriting guidelines member must be 30 years old or younger",
        YOUTH_OVERAGE
    )

    override val validate =
        { data: NorwegianHomeContentsData ->
            (data.isYouth) &&
                data.birthDate.until(LocalDate.now(), ChronoUnit.YEARS) > 30
        }
}
