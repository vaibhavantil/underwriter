package com.hedvig.underwriter.service.quotesSignDataStrategies

import com.hedvig.underwriter.model.Market
import com.hedvig.underwriter.model.Quote
import com.hedvig.underwriter.model.ssn
import com.hedvig.underwriter.service.model.PersonPolicyHolder
import com.hedvig.underwriter.serviceIntegration.memberService.dtos.Nationality

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

fun List<Quote>.safelyNationality(): Nationality {
    val nationality = Nationality.fromQuote(this[0])

    if (this.any { Nationality.fromQuote(it) != nationality }) {
        throw RuntimeException("nationality is not matching when getting nationality from quotes")
    }

    return nationality
}

fun List<Quote>.safelyMarket(): Market {
    val market = this.firstOrNull()?.market ?: throw RuntimeException("Cannot get market from empty list")

    if (this.any { it.market != market }) {
        throw RuntimeException("Quotes belong to different markets: $this")
    }

    return market
}

val List<Quote>.markets: Set<Market>
    get() = this.map { it.market }.toSet()
