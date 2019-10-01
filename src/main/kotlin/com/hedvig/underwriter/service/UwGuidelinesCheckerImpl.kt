package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.repository.CompleteQuoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UwGuidelinesCheckerImpl(): UwGuidelinesChecker {

    override fun meetsHomeUwGuidelines(completeQuote: CompleteQuote): Boolean {
        if(completeQuote.houseHoldSize < 6 && completeQuote.livingSpace < 250) return true

        if (completeQuote.houseHoldSize > 6) {
            completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guideline household size must be less than 6"
        }
        if(completeQuote.livingSpace > 250) {
            completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guideline living space must be less than 250sqm"
        }
        return false
    }

    override fun meetsHouseUwGuidelines(completeQuote: CompleteQuote): Boolean {
//        TODO: complete house uw guidelines
        return true
    }

}