package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.model.Partner
import com.hedvig.underwriter.model.Quote
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.ZoneId

private val logger = KotlinLogging.logger {}

@ConditionalOnProperty(value = ["customerio.username"], matchIfMissing = false)
@Component
@EnableFeignClients
class CustomerIO(val customerIOClient: CustomerIOClient) {

    fun postSignUpdate(memberId: Quote) {
        if(memberId.memberId != null) {
            try {
                val map = mutableMapOf(
                    "partner_code" to memberId.attributedTo.name,
                    "sign_source" to memberId.initiatedFrom.name,
                    "sign_date" to LocalDate.now().atStartOfDay(ZoneId.of("Europe/Stockholm"))
                )
                this.customerIOClient.put(memberId.memberId, map)
            } catch (ex: Exception) {
                logger.error("Could not update \"member\" with id \"${memberId.memberId}\"", ex)
            }
        }
    }
}
