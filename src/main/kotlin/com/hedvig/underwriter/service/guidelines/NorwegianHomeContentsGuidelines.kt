package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianHomeContentsData

object NorwegianHomeContentsGuidelines {
    val setOfRules = setOf(
        NorwegianHomeContentCoinsuredCantBeNegative,
        NorwegianHomeContentLivingSpaceAtLeast1Sqm,
        NorwegianHomeContentsCoinsuredNotMoreThan5,
        NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm
    )
}

object NorwegianHomeContentCoinsuredCantBeNegative : BaseGuideline<NorwegianHomeContentsData> {
    override val errorMessage: String = "coinsured cant be negative"

    override val validate = { data: NorwegianHomeContentsData -> data.coinsured!! < 0 }
}

object NorwegianHomeContentLivingSpaceAtLeast1Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val errorMessage: String = "living space must be at least 1 sqm"

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace!! < 1 }
}

object NorwegianHomeContentsCoinsuredNotMoreThan5 : BaseGuideline<NorwegianHomeContentsData> {
    override val errorMessage: String = "coinsured size must be less than or equal to 5"

    override val validate = { data: NorwegianHomeContentsData -> data.coinsured!! > 5 }
}

object NorwegianHomeContentsLivingSpaceNotMoreThan250Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val errorMessage: String =
        "living space must be less than or equal to 250 sqm"

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace!! > 250 }
}
