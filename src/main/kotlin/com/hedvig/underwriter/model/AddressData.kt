package com.hedvig.underwriter.model

interface AddressData {
    val street: String?
    val zipCode: String?
    val city: String?
}

interface DanishHomeContentAddressData : AddressData {
    override val street: String?
    override val zipCode: String?
    override val city: String?
    val bbrId: String?
}
