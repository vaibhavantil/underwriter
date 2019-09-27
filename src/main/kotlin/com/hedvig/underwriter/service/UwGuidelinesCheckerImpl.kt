package com.hedvig.underwriter.service

import com.hedvig.underwriter.model.CompleteQuote
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class UwGuidelinesCheckerImpl @Autowired constructor (): UwGuidelinesChecker {

//    TODO: put logger messages in
    override fun meetsHomeUwGuidelines(completeQuote: CompleteQuote): Boolean {
        if (completeQuote.houseHoldSize > 6) return false
        if(completeQuote.livingSpace > 250) return false
        return true
    }

    override fun meetsHouseUwGuidelines(completeQuote: CompleteQuote): Boolean {
//        TODO: complete house uw guidelines
        return true
    }

}