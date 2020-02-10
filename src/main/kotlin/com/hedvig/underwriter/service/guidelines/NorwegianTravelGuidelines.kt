package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianTravelData

object NorwegianTravelGuidelines {
    val setOfRules = setOf(
        NorwegianTravelCoinsuredCantBeNegative
    )
}

object NorwegianTravelCoinsuredCantBeNegative : BaseGuideline<NorwegianTravelData> {
    override val errorMessage: String = "coinsured cant be negative"

    override val validate = { data: NorwegianTravelData -> data.coinsured!! < 0 }
}
