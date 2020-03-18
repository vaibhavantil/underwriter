package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.model.Quote
import java.time.LocalDate
import java.time.ZoneId
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@ConditionalOnProperty(value = ["customerio.username"], matchIfMissing = false)
@Component
@EnableFeignClients
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
