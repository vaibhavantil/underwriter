package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.STUDENT_OVERAGE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.STUDENT_TOO_BIG_HOUSE_HOLD_SIZE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.STUDENT_TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object SwedishApartmentGuidelines {
    val setOfRules = setOf(
        SwedishApartmentHouseHoldSizeAtLeast1,
        SwedishApartmentLivingSpaceAtLeast1Sqm,
        SwedishApartmentHouseHoldSizeNotMoreThan6,
        SwedishApartmentLivingSpaceNotMoreThan250Sqm,
        SwedishStudentApartmentHouseholdSizeNotMoreThan2,
        SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm,
        SwedishStudentApartmentAgeNotMoreThan30Years
    )
}

object SwedishApartmentHouseHoldSizeAtLeast1 : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = toSmallNumberOfHouseHoldSize

    override val validate = { data: SwedishApartmentData -> data.householdSize!! < 1 }
}

object SwedishApartmentLivingSpaceAtLeast1Sqm : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = tooSmallLivingSpace

    override val validate = { data: SwedishApartmentData -> data.livingSpace!! < 1 }
}

object SwedishApartmentHouseHoldSizeNotMoreThan6 : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = toHighNumberOfHouseHoldSize

    override val validate = { data: SwedishApartmentData -> data.householdSize!! > 6 }
}

object SwedishApartmentLivingSpaceNotMoreThan250Sqm : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = tooMuchLivingSpace

    override val validate = { data: SwedishApartmentData -> data.livingSpace!! > 250 }
}

object SwedishStudentApartmentHouseholdSizeNotMoreThan2 : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = studentTooBigHouseHoldSize

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.householdSize!! > 2
        }
}

object SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = studentTooMuchLivingSpace

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.livingSpace!! > 50
        }
}

object SwedishStudentApartmentAgeNotMoreThan30Years : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = studentOverage

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.ssn!!.birthDateFromSwedishSsn().until(LocalDate.now(), ChronoUnit.YEARS) > 30
        }
}

private val studentTooMuchLivingSpace = BreachedGuideline(
    "breaches underwriting guideline living space must be less than or equal to 50sqm",
    STUDENT_TOO_MUCH_LIVING_SPACE
)

private val studentOverage = BreachedGuideline(
    "breaches underwriting guidelines member must be 30 years old or younger",
    STUDENT_OVERAGE
)

private val studentTooBigHouseHoldSize = BreachedGuideline(
    "breaches underwriting guideline household size must be less than 2",
    STUDENT_TOO_BIG_HOUSE_HOLD_SIZE
)

private val tooMuchLivingSpace = BreachedGuideline(
    "breaches underwriting guideline living space must be less than or equal to 250 sqm",
    TOO_MUCH_LIVING_SPACE
)

private val toHighNumberOfHouseHoldSize = BreachedGuideline(
    "breaches underwriting guideline household size must be less than or equal to 6",
    TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE
)

private val tooSmallLivingSpace = BreachedGuideline(
    "breaches underwriting guideline living space, must be at least 1 sqm",
    TOO_SMALL_LIVING_SPACE
)

private val toSmallNumberOfHouseHoldSize = BreachedGuideline(
    "breaches underwriting guideline household size, must be at least 1",
    TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE
)
