package com.hedvig.underwriter.model

interface AddressData {
    val street: String?
    val zipCode: String?
    val city: String?
}

interface DanishHomeContentsAddressData : AddressData {
    override val street: String?
    override val zipCode: String?
    override val city: String?
    val bbrId: String?
    val apartmentNumber: String?
    val floor: Int?
}
