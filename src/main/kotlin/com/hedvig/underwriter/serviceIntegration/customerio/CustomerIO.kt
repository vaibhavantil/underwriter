package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.model.Partner
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@ConditionalOnProperty(value = ["customerio.username"], matchIfMissing = false)
@Component
@EnableFeignClients
class CustomerIO(val customerIOClient: CustomerIOClient) {

    fun setPartnerCode(memberId: String, partner: Partner) {
        try {
            val map = mutableMapOf("partner_code" to partner.name)
            this.customerIOClient.put(memberId, map)
        } catch (ex: Exception) {
            logger.error("Could not assign \"partner_code\" to \"${partner.name}\" for $memberId", ex)
        }
    }
}
