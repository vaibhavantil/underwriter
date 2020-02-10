package com.hedvig.underwriter.graphql.type.depricated

import com.hedvig.underwriter.graphql.type.ApartmentType
import com.hedvig.underwriter.service.model.QuoteRequestData

@Deprecated("Use EditSwedishApartmentInput")
data class EditApartmentInput(
    val street: String?,
    val zipCode: String?,
    val householdSize: Int?,
    val livingSpace: Int?,
    val type: ApartmentType?
) {
    fun toQuoteRequestDataDto() =
        QuoteRequestData.SwedishApartment(
            street = this.street,
            zipCode = this.zipCode,
            livingSpace = this.livingSpace,
            householdSize = this.householdSize,
            subType = this.type?.toSubType(),
            city = null,
            floor = null
        )
}
