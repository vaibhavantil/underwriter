package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.hedvig.underwriter.model.NorwegianHomeContentsType

enum class NorwegianHomeContentLineOfBusiness {
    RENT,
    OWN,
    STUDENT_RENT,
    STUDENT_OWN;

    companion object {
        fun from(type: NorwegianHomeContentsType, isStudent: Boolean) = when (type) {
            NorwegianHomeContentsType.RENT -> if (isStudent) STUDENT_RENT else RENT
            NorwegianHomeContentsType.OWN -> if (isStudent) STUDENT_OWN else OWN
        }
    }
}
