package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

enum class NorwegianTravelLineOfBusiness {
    REGULAR,
    YOUTH;

    companion object {
        fun from(isYouth: Boolean): NorwegianTravelLineOfBusiness = if (isYouth) YOUTH else REGULAR
    }
}
