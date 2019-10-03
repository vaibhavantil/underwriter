package com.hedvig.underwriter.serviceIntegration.productPricing.dtos;

import com.hedvig.underwriter.model.LineOfBusiness;
import java.time.LocalDate

data class HomeQuotePriceDto (
        var birthDate: LocalDate,
        var livingSpace: Int,
        var houseHoldSize: Int,
        var zipCode: String,
        var houseType: LineOfBusiness,
        var isStudent: Boolean
)
