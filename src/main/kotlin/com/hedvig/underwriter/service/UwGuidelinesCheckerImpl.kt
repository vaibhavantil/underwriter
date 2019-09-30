package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.utils.Helpers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UwGuidelinesCheckerImpl @Autowired constructor (val helpers: Helpers): UwGuidelinesChecker {

    override fun meetsHomeUwGuidelines(completeQuote: CompleteQuote): Boolean {
        if (completeQuote.houseHoldSize > 6) {
            helpers.logger.info("checking household size uw guideline and meets underwriting guidelines is ${completeQuote.houseHoldSize > 6}")
            return false
        }
        if(completeQuote.livingSpace > 250) {
            helpers.logger.info("checking household size uw guideline and meets underwriting guidelines is ${completeQuote.houseHoldSize > 6}")
            return false
        }
        return true
    }

    override fun meetsHouseUwGuidelines(completeQuote: CompleteQuote): Boolean {
//        TODO: complete house uw guidelines
        return true
    }

}