package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import com.hedvig.underwriter.model.LineOfBusiness
import org.springframework.stereotype.Service

@Service
class UwGuidelinesCheckerImpl() : UwGuidelinesChecker {

    override fun meetsHomeUwGuidelines(completeQuote: CompleteQuote): Boolean {

        return when (completeQuote.lineOfBusiness) {
            LineOfBusiness.RENT, LineOfBusiness.BRF -> {
                if (completeQuote.houseHoldSize > 6) completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guideline household size must be less than 6"
                if (completeQuote.livingSpace > 250) completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guideline living space must be less than 250sqm"
                return completeQuote.houseHoldSize <= 6 && completeQuote.livingSpace <= 250
            }
            LineOfBusiness.STUDENT_RENT, LineOfBusiness.STUDENT_BRF -> {
                if (completeQuote.houseHoldSize > 2) completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guideline household size must be less than 2"
                if (completeQuote.livingSpace > 50) completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guideline living space must be less than 50sqm"
                if (completeQuote.memberIsOver30()) completeQuote.reasonQuoteCannotBeCompleted += "breaches underwriting guidelines member must be under 30"
                completeQuote.houseHoldSize <= 2 && completeQuote.livingSpace <= 50 && !completeQuote.memberIsOver30()
            }
            else -> false
        }
    }

    override fun meetsHouseUwGuidelines(completeQuote: CompleteQuote): Boolean {
//        TODO: complete house uw guidelines
        return true
    }
}