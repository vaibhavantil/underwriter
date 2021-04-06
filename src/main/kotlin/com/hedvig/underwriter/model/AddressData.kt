package com.hedvig.underwriter.model

interface AddressData {
    val street: String?
    val zipCode: String?
    val city: String?
}

interface DanishHomeContentAddressData : AddressData {
    val bbrId: String?
    val apartment: String?
    val floor: String?
}
