package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationServiceClient
import org.springframework.http.ResponseEntity

class NotificationServiceProxyClient(
    private val notificationServiceClient: NotificationServiceClient
):CustomerIOClient {

    override fun put(id: String, data: Map<String, Any?>): ResponseEntity<String> {
        return notificationServiceClient.post(id, data)
    }

}
