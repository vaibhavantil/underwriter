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

object SwedishHouseHouseholdSizeAtLeast1 : SwedishHouseGuideline {
    override val breachedGuideline = TOO_SMALL_NUMBER_OF_HOUSE_HOLD_SIZE

    override val validate = { data: SwedishHouseData -> data.householdSize!! < 1 }
}

object SwedishHouseLivingSpaceAtLeast1Sqm : SwedishHouseGuideline {
    override val breachedGuideline = TOO_SMALL_LIVING_SPACE

    override val validate = { data: SwedishHouseData -> data.livingSpace!! < 1 }
}

object SwedishHouseHouseholdSizeNotMoreThan6 : SwedishHouseGuideline {
    override val breachedGuideline = TOO_HIGH_NUMBER_OF_HOUSE_HOLD_SIZE

    override val validate = { data: SwedishHouseData -> data.householdSize!! > 6 }
}

object SwedishHouseLivingSpaceNotMoreThan250Sqm : SwedishHouseGuideline {
    override val breachedGuideline = TOO_MUCH_LIVING_SPACE

    override val validate = { data: SwedishHouseData -> data.livingSpace!! > 250 }
}

object SwedishHouseYearOfConstruction : SwedishHouseGuideline {
    override val breachedGuideline = TOO_EARLY_YEAR_OF_CONSTRUCTION

    override val validate = { data: SwedishHouseData -> data.yearOfConstruction!! < 1925 }
}

object SwedishHouseNumberOfBathrooms : SwedishHouseGuideline {
    override val breachedGuideline = TOO_MANY_BATHROOMS

    override val validate = { data: SwedishHouseData -> data.numberOfBathrooms!! > 2 }
}

object SwedishHouseNumberOfExtraBuildingsWithAreaOverSixSqm : SwedishHouseGuideline {
    override val breachedGuideline = TOO_MANY_EXTRA_BUILDINGS

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.filter { building -> building.area > 6 }.size > 4 }
}

object SwedishHouseExtraBuildingsSizeNotOverThan75Sqm : SwedishHouseGuideline {
    override val breachedGuideline = TOO_BIG_EXTRA_BUILDING_SIZE

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area > 75 } }
}

object SwedishHouseExtraBuildingsSizeAtLeast1Sqm : SwedishHouseGuideline {
    override val breachedGuideline = TOO_SMALL_EXTRA_BUILDING_SIZE

    override val validate =
        { data: SwedishHouseData -> data.extraBuildings!!.any { building -> building.area < 1 } }
}
