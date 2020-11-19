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
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline household size, must be at least 1",
            "HOUSE_HOLD_SIZE_LESS_THAN_1"
    )

    override val validate = { data: SwedishHouseData -> data.householdSize!! < 1 }
}

object SwedishHouseLivingSpaceAtLeast1Sqm : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline living space, must be at least 1 sqm",
        "LIVING_SPACE_LESS_THAN_1"
    )

    override val validate = { data: SwedishHouseData -> data.livingSpace!! < 1 }
}

object SwedishHouseHouseholdSizeNotMoreThan6 : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline household size, must not be more than 6",
        "HOUSE_HOLD_SIZE_MORE_THAN_6"
    )

    override val validate = { data: SwedishHouseData -> data.householdSize!! > 6 }
}

object SwedishHouseLivingSpaceNotMoreThan250Sqm : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline living space, must not be more than 250 sqm",
            "LIVING_SPACE_MORE_THAN_250"
    )

    override val validate = { data: SwedishHouseData -> data.livingSpace!! > 250 }
}

object SwedishHouseYearOfConstruction : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline year of construction, must not be older than 1925",
        "YEAR_OF_CONSTRUCTION_BEFORE_1925"
    )

    override val validate = { data: SwedishHouseData -> data.yearOfConstruction!! < 1925 }
}

object SwedishHouseNumberOfBathrooms : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline number of bathrooms, must not be more than 2",
        "NUMBER_OF_BATHROOMS_MORE_THAN_2"
    )

    override val validate = { data: SwedishHouseData -> data.numberOfBathrooms!! > 2 }
}

object SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline extra building areas, number of extra buildings with an area over 6 sqm must not be more than 4",
        "NUMBER_OF_EXTRA_BUILDINGS_WITH_A_SIZE_OF_6_SQM_MORE_THAN_4"
    )

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.filter { building -> building.area > 6 }.size > 4 }
}

object SwedishHouseExtraBuildingsSizeNotOverThan75Sqm : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline extra building areas, extra buildings may not be over 75 sqm",
        "EXTRA_BUILDING_SIZE_MORE_THAN_75"
    )

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area > 75 } }
}

object SwedishHouseExtraBuildingsSizeAtLeast1Sqm : SwedishHouseGuideline {
    override val guidelineBreached = GuidelineBreached(
        "breaches underwriting guideline extra building areas, extra buildings must have an area of at least 1",
        "EXTRA_BUILDING_SIZE_LESS_THAN_1"
    )

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area < 1 } }
}
