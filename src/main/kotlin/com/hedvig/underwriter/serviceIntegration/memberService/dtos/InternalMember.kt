package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.libs.logging.masking.Masked
import java.time.Instant
import java.time.LocalDate

enum class Gender {
    MALE,
    FEMALE
}

data class InternalMember(
    val memberId: Long,
    val status: String?,
    @Masked val ssn: String,
    val gender: Gender?,
    @Masked val firstName: String,
    @Masked val lastName: String,
    @Masked val street: String?,
    val floor: Int?,
    val apartment: String?,
    val city: String?,
    val zipCode: String?,
    val country: String?,
    @Masked val email: String?,
    @Masked val phoneNumber: String?,
    val birthDate: LocalDate,
    val signedOn: Instant?,
    val createdOn: Instant?,
    val fraudulentStatus: String?,
    val fraudulentDescription: String?,
    val acceptLanguage: String?
)
