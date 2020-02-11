package com.hedvig.underwriter.graphql.type

import com.hedvig.underwriter.model.ApartmentProductSubType

enum class ApartmentType {
    STUDENT_RENT,
    RENT,
    STUDENT_BRF,
    BRF;

    fun toSubType(): ApartmentProductSubType = when (this) {
        STUDENT_RENT -> ApartmentProductSubType.STUDENT_RENT
        RENT -> ApartmentProductSubType.RENT
        STUDENT_BRF -> ApartmentProductSubType.STUDENT_BRF
        BRF -> ApartmentProductSubType.BRF
    }
}
