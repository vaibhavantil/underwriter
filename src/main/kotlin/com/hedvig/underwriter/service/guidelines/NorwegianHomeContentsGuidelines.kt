package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.NorwegianHomeContentsData

object NorwegianHomeContentsGuidelines {
    val setOfRules = setOf(
        NorwegianLivingSpaceAtLeast1Sqm
    )
}

object NorwegianLivingSpaceAtLeast1Sqm : BaseGuideline<NorwegianHomeContentsData> {
    override val errorMessage: String = "breaches underwriting guideline living space, must be at least 1 sqm"

    override val validate = { data: NorwegianHomeContentsData -> data.livingSpace!! < 1 }
}
