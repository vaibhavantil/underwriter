package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.SwedishHouseData
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_BIG_EXTRA_BUILDING_SIZE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_EARLY_YEAR_OF_CONSTRUCTION
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MANY_BATHROOMS
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MANY_EXTRA_BUILDINGS
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_MUCH_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_EXTRA_BUILDING_SIZE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_LIVING_SPACE
import com.hedvig.underwriter.service.guidelines.BreachedGuidelinesCodes.TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE

typealias SwedishHouseGuideline = BaseGuideline<SwedishHouseData>

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

private val tooSmallNumberOfHouseHoldSize = BreachedGuideline(
    "breaches underwriting guideline household size, must be at least 1",
    TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE
)
private val tooSmallLivingSpace = BreachedGuideline(
    "breaches underwriting guideline living space, must be at least 1 sqm",
    TOO_SMALL_LIVING_SPACE
)
private val tooHighNumberOfHouseHoldSize = BreachedGuideline(
    "breaches underwriting guideline household size, must not be more than 6",
    TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE
)
private val tooMuchLivingSpace = BreachedGuideline(
    "breaches underwriting guideline living space, must not be more than 250 sqm",
    TOO_MUCH_LIVING_SPACE
)
private val tooEarlyYearOfConstruction = BreachedGuideline(
    "breaches underwriting guideline year of construction, must not be older than 1925",
    TOO_EARLY_YEAR_OF_CONSTRUCTION
)
private val tooManyBathrooms = BreachedGuideline(
    "breaches underwriting guideline number of bathrooms, must not be more than 2",
    TOO_MANY_BATHROOMS
)
private val tooManyExtraBuildings = BreachedGuideline(
    "breaches underwriting guideline extra building areas, number of extra buildings with an area over 6 sqm must not be more than 4",
    TOO_MANY_EXTRA_BUILDINGS
)
private val tooBigExtraBuildingSize = BreachedGuideline(
    "breaches underwriting guideline extra building areas, extra buildings may not be over 75 sqm",
    TOO_BIG_EXTRA_BUILDING_SIZE
)
private val tooSmallExtraBuildingSize = BreachedGuideline(
    "breaches underwriting guideline extra building areas, extra buildings must have an area of at least 1",
    TOO_SMALL_EXTRA_BUILDING_SIZE
)

object SwedishHouseHouseholdSizeAtLeast1 : SwedishHouseGuideline {
    override val breachedGuideline = tooSmallNumberOfHouseHoldSize

    override val validate = { data: SwedishHouseData -> data.householdSize!! < 1 }
}

object SwedishHouseLivingSpaceAtLeast1Sqm : SwedishHouseGuideline {
    override val breachedGuideline = tooSmallLivingSpace

    override val validate = { data: SwedishHouseData -> data.livingSpace!! < 1 }
}

object SwedishHouseHouseholdSizeNotMoreThan6 : SwedishHouseGuideline {
    override val breachedGuideline = tooHighNumberOfHouseHoldSize

    override val validate = { data: SwedishHouseData -> data.householdSize!! > 6 }
}

object SwedishHouseLivingSpaceNotMoreThan250Sqm : SwedishHouseGuideline {
    override val breachedGuideline = tooMuchLivingSpace

    override val validate = { data: SwedishHouseData -> data.livingSpace!! > 250 }
}

object SwedishHouseYearOfConstruction : SwedishHouseGuideline {
    override val breachedGuideline = tooEarlyYearOfConstruction

    override val validate = { data: SwedishHouseData -> data.yearOfConstruction!! < 1925 }
}

object SwedishHouseNumberOfBathrooms : SwedishHouseGuideline {
    override val breachedGuideline = tooManyBathrooms

    override val validate = { data: SwedishHouseData -> data.numberOfBathrooms!! > 2 }
}

object SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm : SwedishHouseGuideline {
    override val breachedGuideline = tooManyExtraBuildings

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.filter { building -> building.area > 6 }.size > 4 }
}

object SwedishHouseExtraBuildingsSizeNotOverThan75Sqm : SwedishHouseGuideline {
    override val breachedGuideline = tooBigExtraBuildingSize

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area > 75 } }
}

object SwedishHouseExtraBuildingsSizeAtLeast1Sqm : SwedishHouseGuideline {
    override val breachedGuideline = tooSmallExtraBuildingSize

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area < 1 } }
}
