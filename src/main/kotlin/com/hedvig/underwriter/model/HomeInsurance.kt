package com.hedvig.underwriter.model

interface HomeInsurance : AddressInsurance {
    val householdSize: Int?
}
