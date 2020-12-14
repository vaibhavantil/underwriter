package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.ssn
import com.hedvig.underwriter.service.model.PersonPolicyHolder

fun List<Quote>.safelyGetSsn(): String {

    val ssn = this.first { it.data is PersonPolicyHolder<*> }.ssn

    if (this.any { (it.data as PersonPolicyHolder<*>?)?.let { it.ssn != ssn } == true }) {
        throw RuntimeException("ssn is not matching when getting ssn from quotes")
    }

    return ssn
}

fun List<Quote>.safelyGetMemberId(): Long {
    val memberId = this[0].memberId!!

    if (this.any { it.memberId != memberId }) {
        throw RuntimeException("memberId is not matching when getting memberid from quotes")
    }

    return memberId.toLong()
}
