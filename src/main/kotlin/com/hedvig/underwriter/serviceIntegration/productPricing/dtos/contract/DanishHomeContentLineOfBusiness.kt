package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.hedvig.underwriter.model.DanishHomeContentsType

enum class DanishHomeContentLineOfBusiness {
    RENT,
    OWN,
    STUDENT_RENT,
    STUDENT_OWN;

    companion object {
        fun from(type: DanishHomeContentsType, isStudent: Boolean) = when (type) {
            DanishHomeContentsType.RENT -> if (isStudent) STUDENT_RENT else RENT
            DanishHomeContentsType.OWN -> if (isStudent) STUDENT_OWN else OWN
        }
    }
}
