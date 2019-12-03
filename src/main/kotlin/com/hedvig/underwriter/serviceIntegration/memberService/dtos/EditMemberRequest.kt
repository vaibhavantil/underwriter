package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import java.time.Instant
import java.time.LocalDate

class EditMemberRequest(
    val ssn: String,
    val firstName: String,
    val lastName: String,
    val street: String,
    val zipCode: String,
    val birthDate: LocalDate,
    val city: String? = null,
    val floor: Int? = null,
    val apartment: String? = null,
    val country: String? = null,
    val phoneNumber: String? = null,
    val createdOn: Instant? = null,
    val fraudulentStatus: String? = null,
    val fraudulentDescription: String? = null,
    val acceptLanguage: String? = null
)
