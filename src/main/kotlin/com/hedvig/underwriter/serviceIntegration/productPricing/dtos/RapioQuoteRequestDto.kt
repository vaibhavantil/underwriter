package com.hedvig.underwriter.serviceIntegration.productPricing.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.HouseData
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.QuoteInitiatedFrom
import com.hedvig.underwriter.model.birthDateFromSsn
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
) {
    companion object {
        fun from(quote: Quote, email: String): RapioQuoteRequestDto {
            return when (val data = quote.data) {
                is HouseData -> {
                    RapioQuoteRequestDto(
                        quote.price!!,
                        data.firstName!!,
                        data.lastName!!,
                        data.ssn!!.birthDateFromSsn(),
                        false,
                        Address(
                            data.street!!,
                            data.city!!,
                            data.zipCode!!,
                            0
                        ),
                        data.livingSpace!!.toFloat(),
                        ProductPricingProductTypes.HOUSE,
                        quote.currentInsurer,
                        data.householdSize!!,
                        quote.startDate?.atStartOfDay(),
                        data.ssn,
                        email,
                        "",
                        quote.initiatedFrom,
                        data.ancillaryArea,
                        data.yearOfConstruction,
                        data.numberOfBathrooms,
                        data.extraBuildings?.map { extraBuilding ->
                            ExtraBuildingDTO(
                                com.hedvig.underwriter.graphql.type.ExtraBuildingType.valueOf(extraBuilding.type.name),
                                extraBuilding.area,
                                extraBuilding.hasWaterConnected
                            )
                        },
                        data.isSubleted
                    )
                }
                is ApartmentData -> {
                    RapioQuoteRequestDto(
                        quote.price!!,
                        data.firstName!!,
                        data.lastName!!,
                        data.ssn!!.birthDateFromSsn(),
                        false,
                        Address(
                            data.street!!,
                            data.city!!,
                            data.zipCode!!,
                            0
                        ),
                        data.livingSpace!!.toFloat(),
                        when (data.subType!!) {
                            ApartmentProductSubType.BRF -> ProductPricingProductTypes.BRF
                            ApartmentProductSubType.RENT -> ProductPricingProductTypes.RENT
                            ApartmentProductSubType.RENT_BRF -> TODO()
                            ApartmentProductSubType.SUBLET_RENTAL -> ProductPricingProductTypes.STUDENT_RENT
                            ApartmentProductSubType.SUBLET_BRF -> ProductPricingProductTypes.STUDENT_BRF
                            ApartmentProductSubType.STUDENT_BRF -> ProductPricingProductTypes.STUDENT_BRF
                            ApartmentProductSubType.STUDENT_RENT -> ProductPricingProductTypes.STUDENT_RENT
                            ApartmentProductSubType.LODGER -> TODO()
                            ApartmentProductSubType.UNKNOWN -> TODO()
                        },
                        quote.currentInsurer,
                        data.householdSize!!,
                        quote.startDate?.atStartOfDay(),
                        data.ssn,
                        email,
                        "",
                        quote.initiatedFrom,
                        null,
                        null,
                        null,
                        null,
                        null
                    )
                }
                else -> throw RuntimeException("Incomplete quote is of unknown type: ${quote.data::class}")
            }
        }
    }
}
