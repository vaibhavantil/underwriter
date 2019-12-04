package com.hedvig.underwriter.serviceIntegration.memberService.dtos

import com.hedvig.underwriter.graphql.type.CreateQuoteInput
import com.hedvig.underwriter.model.birthDateFromSsn
import java.lang.IllegalStateException
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
) {
    companion object {
        fun fromCreateQuoteinput(createQuoteInput: CreateQuoteInput) =
            createQuoteInput.apartment?.let { apartment ->
                toEditMemberRequest(createQuoteInput, apartment.street, apartment.zipCode)
            } ?: createQuoteInput.house?.let { house ->
                toEditMemberRequest(createQuoteInput, house.street, house.zipCode)
            } ?: throw IllegalStateException("Trying to create EditMemberRequest without apartment and house!")

        private fun toEditMemberRequest(createQuoteInput: CreateQuoteInput, street: String, zipCode: String) = EditMemberRequest(
            ssn = createQuoteInput.ssn,
            firstName = createQuoteInput.firstName,
            lastName = createQuoteInput.lastName,
            street = street,
            zipCode = zipCode,
            birthDate = createQuoteInput.ssn.birthDateFromSsn()
        )
    }
}
