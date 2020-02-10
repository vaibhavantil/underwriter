package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianTravelData

object NorwegianTravelGuidelines {
    val setOfRules = setOf(
        NorwegianTravelcoInsuredCantBeNegative
    )
}

object NorwegianTravelcoInsuredCantBeNegative : BaseGuideline<NorwegianTravelData> {
    override val errorMessage: String = "coInsured cant be negative"

    override val validate = { data: NorwegianTravelData -> data.coInsured!! < 0 }
}
