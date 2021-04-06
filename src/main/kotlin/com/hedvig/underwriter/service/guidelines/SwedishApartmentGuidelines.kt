package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.birthDateFromSwedishSsn
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.OK
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
        SwedishApartmentHouseHoldGuideline,
        SwedishApartmentLivingSpaceGuideline,
        SwedishStudentApartmentAgeNotMoreThan30Years
    )
}

object SwedishApartmentHouseHoldGuideline : BaseGuideline<SwedishApartmentData> {

    override fun validate(data: SwedishApartmentData): BreachedGuidelineCode? {
        if (data.householdSize!! < 1) {
            return TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE
        }

        if (!data.isStudent && data.householdSize > 6) {
            return TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE
        }

        if (data.isStudent && data.householdSize > 2) {
            return STUDENT_TOO_BIG_HOUSE_HOLD_SIZE
        }

        return OK
    }
}

object SwedishApartmentLivingSpaceGuideline : BaseGuideline<SwedishApartmentData> {

    override fun validate(data: SwedishApartmentData): BreachedGuidelineCode? {
        if (data.livingSpace!! < 1) {
            return TOO_SMALL_LIVING_SPACE
        }

        if (!data.isStudent && data.livingSpace > 250) {
            return TOO_MUCH_LIVING_SPACE
        }

        if (data.isStudent && data.livingSpace > 50) {
            return STUDENT_TOO_MUCH_LIVING_SPACE
        }

        return OK
    }
}

object SwedishStudentApartmentAgeNotMoreThan30Years : BaseGuideline<SwedishApartmentData> {

    override fun validate(data: SwedishApartmentData): BreachedGuidelineCode? {
        if (data.isStudent && data.ssn!!.birthDateFromSwedishSsn().until(LocalDate.now(), ChronoUnit.YEARS) > 30) {
            return STUDENT_OVERAGE
        }
        return OK
    }
}
