package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianTravelData
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object NorwegianTravelGuidelines {
    val setOfRules = setOf(
        NorwegianTravelcoInsuredCantBeNegative,
        NorwegianYouthTravelAgeNotMoreThan34Years
    )
}

object NorwegianTravelcoInsuredCantBeNegative : BaseGuideline<NorwegianTravelData> {
    override val errorMessage: String = "coInsured cant be negative"

    override val validate = { data: NorwegianTravelData -> data.coInsured < 0 }
}

object NorwegianYouthTravelAgeNotMoreThan34Years : BaseGuideline<NorwegianTravelData> {
    override val errorMessage: String =
        "breaches underwriting guidelines member must be 34 years old or younger"

    override val validate =
        { data: NorwegianTravelData ->
            (data.isYouth) &&
                data.birthDate.until(
                    LocalDate.now(),
                    ChronoUnit.YEARS
                ) > 34
        }
}
