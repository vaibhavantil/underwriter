package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.hedvig.underwriter.model.LineOfBusiness
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class RapioQuoteRequestDto(
        val currentTotalPrice: BigDecimal,
        val firstName: String,
        val lastName: String,
        var birthDate: LocalDate,
        var isStudent: Boolean,
        val address: Address,
        var livingSpace: Float,
        var houseType: LineOfBusiness,
        val currentInsurer: String?,
        var houseHoldSize: Int,
        var activeFrom: LocalDateTime,
        val ssn: String,
        var emailAddress: String,
        var phoneNumber: String
)

