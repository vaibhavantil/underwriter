package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.SwedishHouseData

interface SwedishHouseGuideline : BaseGuideline<SwedishHouseData>

object SwedishHouseGuidelines {
    val setOfRules = setOf(
        SwedishHouseHouseholdSizeAtLeast1,
        SwedishHouseLivingSpaceAtLeast1Sqm,
        SwedishHouseHouseholdSizeNotMoreThan6,
        SwedishHouseLivingSpaceNotMoreThan250Sqm,
        SwedishHouseYearOfConstruction,
        SwedishHouseNumberOfBathrooms,
        SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm,
        SwedishHouseExtraBuildingsSizeNotOverThan75Sqm,
        SwedishHouseExtraBuildingsSizeAtLeast1Sqm
    )
}

object SwedishHouseHouseholdSizeAtLeast1 : SwedishHouseGuideline {
    override val errorMessage: String = "breaches underwriting guideline household size, must be at least 1"

    override val validate = { data: SwedishHouseData -> data.householdSize!! < 1 }
}

object SwedishHouseLivingSpaceAtLeast1Sqm : SwedishHouseGuideline {
    override val errorMessage: String = "breaches underwriting guideline living space, must be at least 1 sqm"

    override val validate = { data: SwedishHouseData -> data.livingSpace!! < 1 }
}

object SwedishHouseHouseholdSizeNotMoreThan6 : SwedishHouseGuideline {
    override val errorMessage: String = "breaches underwriting guideline household size, must not be more than 6"

    override val validate = { data: SwedishHouseData -> data.householdSize!! > 6 }
}

object SwedishHouseLivingSpaceNotMoreThan250Sqm : SwedishHouseGuideline {
    override val errorMessage: String = "breaches underwriting guideline living space, must not be more than 250 sqm"

    override val validate = { data: SwedishHouseData -> data.livingSpace!! > 250 }
}

object SwedishHouseYearOfConstruction : SwedishHouseGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline year of construction, must not be older than 1925"

    override val validate = { data: SwedishHouseData -> data.yearOfConstruction!! < 1925 }
}

object SwedishHouseNumberOfBathrooms : SwedishHouseGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline number of bathrooms, must not be more than 2"

    override val validate = { data: SwedishHouseData -> data.numberOfBathrooms!! > 2 }
}

object SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm : SwedishHouseGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline extra building areas, number of extra buildings with an area over 6 sqm must not be more than 4"

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.filter { building -> building.area > 6 }.size > 4 }
}

object SwedishHouseExtraBuildingsSizeNotOverThan75Sqm : SwedishHouseGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline extra building areas, extra buildings may not be over 75 sqm"

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area > 75 } }
}

object SwedishHouseExtraBuildingsSizeAtLeast1Sqm : SwedishHouseGuideline {
    override val errorMessage: String =
        "breaches underwriting guideline extra building areas, extra buildings must have an area of at least 1"

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area < 1 } }
}
