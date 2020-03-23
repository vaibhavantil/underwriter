package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

enum class NorwegianTravelLineOfBusiness {
    REGULAR,
    YOUTH;

    companion object {
        fun from(isYouth: Boolean): NorwegianTravelLineOfBusiness = when (isYouth) {
            true -> YOUTH
            false -> REGULAR
        }
    }
}
