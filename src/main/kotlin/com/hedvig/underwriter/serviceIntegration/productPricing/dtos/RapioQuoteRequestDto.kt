package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

data class RapioQuoteRequestDto(
    val currentTotalPrice: BigDecimal,
    val firstName: String,
    val lastName: String,
    val birthDate: LocalDate,
    @get:JsonProperty("isStudent")
    val isStudent: Boolean,
    val address: Address,
    val livingSpace: Float,
    val houseType: ProductPricingProductTypes,
    val currentInsurer: String?,
    val houseHoldSize: Int,
    val activeFrom: LocalDateTime?,
    val ssn: String,
    val emailAddress: String,
    val phoneNumber: String,
    val quoteInitiatedFrom: QuoteInitiatedFrom,
    val ancillaryArea: Int?,
    val yearOfConstruction: Int?,
    val numberOfBathrooms: Int?,
    val extraBuildings: List<ExtraBuildingDTO>?,
    @get:JsonProperty("isSubleted")
    val isSubleted: Boolean?
)
