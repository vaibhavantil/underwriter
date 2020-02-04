package com.hedvig.underwriter.service.guideline

import com.hedvig.underwriter.model.ApartmentData
import com.hedvig.underwriter.model.QuoteData
import com.hedvig.underwriter.service.model.PersonPolicyHolder

interface Guideline<in T : QuoteData> {

    val errorMessage: String
    val validate: (T) -> Boolean

    val skipAfter: Boolean
        get() = false

    fun invokeValidate(data: T): String? {
        if (validate.invoke(data)) {
            return errorMessage
        }
        return null
    }
}

class AgeRestrictionGuideline : Guideline<QuoteData> {
    override val errorMessage = "member is younger than 18"

    override val skipAfter: Boolean
        get() = true

    override val validate = { data: QuoteData -> (data as PersonPolicyHolder<*>).age() < 18 }
}

class SwedishApartmentHouseHoldSize : Guideline<ApartmentData> {
    override val errorMessage: String = "breaches underwriting guideline household size, must be at least 1"

    override val validate = { data: ApartmentData -> data.householdSize!! < 1 }
}

