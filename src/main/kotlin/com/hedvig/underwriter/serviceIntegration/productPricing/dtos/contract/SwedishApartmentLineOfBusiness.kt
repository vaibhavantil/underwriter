package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.contract

import com.hedvig.underwriter.model.ApartmentProductSubType

enum class SwedishApartmentLineOfBusiness {
    RENT,
    BRF,
    STUDENT_RENT,
    STUDENT_BRF;

    companion object {
        fun from(type: ApartmentProductSubType) = when (type) {
            ApartmentProductSubType.BRF -> BRF
            ApartmentProductSubType.RENT -> RENT
            ApartmentProductSubType.STUDENT_BRF -> STUDENT_BRF
            ApartmentProductSubType.STUDENT_RENT -> STUDENT_RENT
        }
    }
}
