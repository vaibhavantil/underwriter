package com.hedvig.underwriter.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
    JsonSubTypes.Type(value = ApartmentData::class, name = "home"),
    JsonSubTypes.Type(value = HouseData::class, name = "house")
)
sealed class QuoteData {
    abstract val isComplete: Boolean
    abstract val id: UUID

    fun productType(): ProductType {
        return when (this) {
            is HouseData -> ProductType.HOUSE
            is ApartmentData -> ProductType.HOME
        }
    }

    abstract fun passUwGuidelines(): List<String>
}

interface PersonPolicyHolder<T : QuoteData> {
    val ssn: String?
    val firstName: String?
    val lastName: String?

    fun updateName(firstName: String, lastName: String): T

    fun age(): Long {
        val dateToday = LocalDate.now()

        return this.ssn!!.birthDateFromSsn().until(dateToday, ChronoUnit.YEARS)
    }

    fun ssnIsValid(): Boolean {
        val trimmedInput = ssn!!.trim().replace("-", "").replace(" ", "")

        if (trimmedInput.length != 12) {
            // reasonQuoteCannotBeCompleted += "ssn not valid"
            return false
        }

        try {
            LocalDate.parse(
                trimmedInput.substring(0, 4) + "-" + trimmedInput.substring(
                    4,
                    6
                ) + "-" + trimmedInput.substring(6, 8)
            )
        } catch (exception: Exception) {
            // reasonQuoteCannotBeCompleted += "ssn not valid"
            return false
        }
        return true
    }
}

interface HomeInsurance {
    val street: String?
    val zipCode: String?
    val city: String?
    val livingSpace: Int?
    val householdSize: Int?
}

data class HouseData(
    override val id: UUID,
    override val ssn: String?,
    override val firstName: String?,
    override val lastName: String?,

    override val street: String?,
    override val zipCode: String?,
    override val city: String?,
    override var livingSpace: Int?,
    override var householdSize: Int?
) : QuoteData(), HomeInsurance, PersonPolicyHolder<HouseData> {
    @get:JsonIgnore
    override val isComplete: Boolean
        get() = when (null) {
            ssn, firstName, lastName, street, zipCode, householdSize, livingSpace -> false
            else -> true
        }

    override fun updateName(firstName: String, lastName: String) = this.copy(firstName = firstName, lastName = lastName)

    override fun passUwGuidelines() = listOf("You shall not pass!")
}

data class ApartmentData(
    override val id: UUID,
    override val ssn: String? = null,
    override val firstName: String? = null,
    override val lastName: String? = null,

    override val street: String? = null,
    override val city: String? = null,
    override val zipCode: String? = null,
    override val householdSize: Int? = null,
    override val livingSpace: Int? = null,

    val subType: ApartmentProductSubType? = null
) : QuoteData(), HomeInsurance, PersonPolicyHolder<ApartmentData> {
    @get:JsonIgnore
    override val isComplete: Boolean
        get() = when (null) {
            ssn, firstName, lastName, street, zipCode, householdSize, livingSpace, subType -> false
            else -> true
        }

    @get:JsonProperty(value = "isStudent")
    val isStudent: Boolean
        get() = subType == ApartmentProductSubType.STUDENT_BRF || subType == ApartmentProductSubType.STUDENT_RENT

    override fun updateName(firstName: String, lastName: String): ApartmentData {
        return this.copy(firstName = firstName, lastName = lastName)
    }

    override fun passUwGuidelines(): List<String> {
        val errors = mutableListOf<String>()

        when (this.subType) {
            ApartmentProductSubType.STUDENT_RENT, ApartmentProductSubType.STUDENT_BRF -> {
                if (this.householdSize!! > 2) errors.add("breaches underwriting guideline household size must be less than 2")
                if (this.livingSpace!! > 50) errors.add("breaches underwriting guideline living space must be less than 50sqm")
                if (this.ssn!!.birthDateFromSsn().until(
                        LocalDate.now(),
                        ChronoUnit.YEARS
                    ) > 30
                ) errors.add("breaches underwriting guidelines member must be under 30")
            }
            else -> {
                if (this.householdSize!! > 6) errors.add("breaches underwriting guideline household size must be less than 6")
                if (this.livingSpace!! > 250) errors.add("breaches underwriting guideline living space must be less than 250sqm")
            }
        }

        return errors
    }
}
