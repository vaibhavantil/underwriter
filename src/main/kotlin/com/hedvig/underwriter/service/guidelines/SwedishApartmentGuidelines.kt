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
    override val breachedGuideline = TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE

    override val validate = { data: SwedishApartmentData -> data.householdSize!! < 1 }
}

object SwedishApartmentLivingSpaceAtLeast1Sqm : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = TOO_SMALL_LIVING_SPACE

    override val validate = { data: SwedishApartmentData -> data.livingSpace!! < 1 }
}

object SwedishApartmentHouseHoldSizeNotMoreThan6 : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE

    override val validate = { data: SwedishApartmentData -> data.householdSize!! > 6 }
}

object SwedishApartmentLivingSpaceNotMoreThan250Sqm : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = TOO_MUCH_LIVING_SPACE

    override val validate = { data: SwedishApartmentData -> data.livingSpace!! > 250 }
}

object SwedishStudentApartmentHouseholdSizeNotMoreThan2 : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = STUDENT_TOO_BIG_HOUSE_HOLD_SIZE

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.householdSize!! > 2
        }
}

object SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = STUDENT_TOO_MUCH_LIVING_SPACE

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.livingSpace!! > 50
        }
}

object SwedishStudentApartmentAgeNotMoreThan30Years : BaseGuideline<SwedishApartmentData> {
    override val breachedGuideline = STUDENT_OVERAGE

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.ssn!!.birthDateFromSwedishSsn().until(LocalDate.now(), ChronoUnit.YEARS) > 30
        }
}
