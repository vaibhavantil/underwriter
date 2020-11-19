package com.hedvig.underwriter.serviceIntegration.productPricing.dtos.mappers

import com.hedvig.productPricingObjects.enums.DanishHomeContentLineOfBusiness
import com.hedvig.productPricingObjects.enums.NorwegianHomeContentLineOfBusiness
import com.hedvig.productPricingObjects.enums.SwedishApartmentLineOfBusiness
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.DanishHomeContentsType
import com.hedvig.underwriter.model.NorwegianHomeContentsType

class LineOfBusinessMapper {
    companion object {
        fun toLineOfBusiness(type: ApartmentProductSubType) = when (type) {
            ApartmentProductSubType.BRF -> SwedishApartmentLineOfBusiness.BRF
            ApartmentProductSubType.RENT -> SwedishApartmentLineOfBusiness.RENT
            ApartmentProductSubType.STUDENT_BRF -> SwedishApartmentLineOfBusiness.STUDENT_BRF
            ApartmentProductSubType.STUDENT_RENT -> SwedishApartmentLineOfBusiness.STUDENT_RENT
        }

        fun toLineOfBusiness(type: NorwegianHomeContentsType, isYouth: Boolean) = when (type) {
            NorwegianHomeContentsType.RENT -> if (isYouth) NorwegianHomeContentLineOfBusiness.YOUTH_RENT else NorwegianHomeContentLineOfBusiness.RENT
            NorwegianHomeContentsType.OWN -> if (isYouth) NorwegianHomeContentLineOfBusiness.YOUTH_OWN else NorwegianHomeContentLineOfBusiness.OWN
        }

        fun toLineOfBusiness(type: DanishHomeContentsType, isStudent: Boolean) = when (type) {
            DanishHomeContentsType.RENT -> if (isStudent) DanishHomeContentLineOfBusiness.STUDENT_RENT else DanishHomeContentLineOfBusiness.RENT
            DanishHomeContentsType.OWN -> if (isStudent) DanishHomeContentLineOfBusiness.STUDENT_OWN else DanishHomeContentLineOfBusiness.OWN
        }
    }
}
