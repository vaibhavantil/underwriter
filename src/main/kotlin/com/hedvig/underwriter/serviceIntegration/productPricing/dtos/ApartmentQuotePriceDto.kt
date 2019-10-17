package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.ApartmentProductSubType
import java.time.LocalDate

data class ApartmentQuotePriceDto(
    var birthDate: LocalDate,
    var livingSpace: Int,
    var houseHoldSize: Int,
    var zipCode: String,
    var houseType: ApartmentProductSubType,
    var isStudent: Boolean
)
