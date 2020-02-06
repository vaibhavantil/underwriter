package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.ApartmentProductSubType
import com.hedvig.underwriter.model.SwedishApartmentData
import com.hedvig.underwriter.model.birthDateFromSsn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface SwedishApartmentGuideline : BaseGuideline<SwedishApartmentData>

object SwedishApartmentHouseHoldSizeAtLeast1 : SwedishApartmentGuideline {
    override val errorMessage: String = "breaches underwriting guideline household size, must be at least 1"

    override val validate = { data: SwedishApartmentData -> data.householdSize!! < 1 }
}

object SwedishApartmentLivingSpaceAtLeast1Sqm : SwedishApartmentGuideline {
    override val errorMessage: String = "breaches underwriting guideline living space, must be at least 1 sqm"

    override val validate = { data: SwedishApartmentData -> data.livingSpace!! < 1 }
}

object SwedishApartmentHouseHoldSizeNotMoreThan6 : SwedishApartmentGuideline {
    override val errorMessage: String = "breaches underwriting guideline household size must be less than or equal to 6"

    override val validate = { data: SwedishApartmentData -> data.householdSize!! > 6 }
}

object SwedishApartmentLivingSpaceNotMoreThan250Sqm : SwedishApartmentGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline living space must be less than or equal to 250 sqm"

    override val validate = { data: SwedishApartmentData -> data.livingSpace!! > 250 }
}

object SwedishStudentApartmentHouseholdSizeNotMoreThan2 : SwedishApartmentGuideline {
    override val errorMessage: String = "breaches underwriting guideline household size must be less than 2"

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.householdSize!! > 2
        }
}

object SwedishStudentApartmentLivingSpaceNotMoreThan50Sqm : SwedishApartmentGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline living space must be less than or equal to 50sqm"

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.livingSpace!! > 50
        }
}

object SwedishStudentApartmentAgeNotMoreThan30Years : SwedishApartmentGuideline {
    override val errorMessage: String =
        "breaches underwriting guidelines member must be 30 years old or younger"

    override val validate =
        { data: SwedishApartmentData ->
            (data.subType == ApartmentProductSubType.STUDENT_RENT || data.subType == ApartmentProductSubType.STUDENT_BRF) &&
                data.ssn!!.birthDateFromSsn().until(
                    LocalDate.now(),
                    ChronoUnit.YEARS
                ) > 30
        }
}
