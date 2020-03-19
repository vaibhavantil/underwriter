package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.model.Quote
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId

private val logger = KotlinLogging.logger {}

@Component
class CustomerIO(val customerIOClient: CustomerIOClient) {

    fun postSignUpdate(quote: Quote) {
        if (quote.memberId != null) {
            val isSwitcher = quote.currentInsurer != null
            try {
                val map = mapOf(
                    "partner_code" to quote.attributedTo.name,
                    "sign_source" to quote.initiatedFrom.name,
                    "sign_date" to LocalDate.now().atStartOfDay(ZoneId.of("Europe/Stockholm")).toEpochSecond(),
                    "switcher_company" to quote.currentInsurer,
                    "is_switcher" to isSwitcher
                )
                this.customerIOClient.put(quote.memberId, map)
            } catch (ex: Exception) {
                logger.error("Could not update \"member\" with id \"${quote.memberId}\"", ex)
            }
        }
    }
}
