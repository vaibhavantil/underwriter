package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationServiceClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@ConditionalOnProperty(value = ["customerio.username"], havingValue = "randomValueToMakeConditionWork", matchIfMissing = true)
@Component
class NotificationServiceProxyClient(
    private val notificationServiceClient: NotificationServiceClient
) : CustomerIOClient {

    override fun put(id: String, data: Map<String, Any?>): ResponseEntity<String> {
        return notificationServiceClient.post(id, data)
    }
}
