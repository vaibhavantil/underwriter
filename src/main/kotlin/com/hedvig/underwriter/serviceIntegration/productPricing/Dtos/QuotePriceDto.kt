package com.hedvig.underwriter.serviceIntegration.productPricing.Dtos;

import com.hedvig.underwriter.model.LineOfBusiness;
import java.time.LocalDate

data class QuotePriceDto (
        var birthDate: LocalDate,
        var livingSpace: Int,
        var houseHoldSize: Int,
        var zipCode: String,
        var floor: Int,
        var houseType: LineOfBusiness,
        var isStudent: Boolean
)
