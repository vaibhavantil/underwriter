package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.hedvig.underwriter.model.NorwegianHomeContentsType

enum class NorwegianHomeContentLineOfBusiness {
    RENT,
    OWN,
    YOUTH_RENT,
    YOUTH_OWN;

    companion object {
        fun from(type: NorwegianHomeContentsType, isYouth: Boolean) = when (type) {
            NorwegianHomeContentsType.RENT -> if (isYouth) YOUTH_RENT else RENT
            NorwegianHomeContentsType.OWN -> if (isYouth) YOUTH_OWN else OWN
        }
    }
}
