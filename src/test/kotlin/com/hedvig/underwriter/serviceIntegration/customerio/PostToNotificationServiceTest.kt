package com.hedvig.underwriter.serviceIntegration.customerio

import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationServiceClient as IntegrationClient
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.springframework.http.ResponseEntity

class PostToNotificationServiceTest {

    @MockK
    lateinit var notificationServiceClient: IntegrationClient

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun put() {

        every { notificationServiceClient.post(any(), any()) } returns ResponseEntity.accepted().build()

        val sut = NotificationServiceProxyClient(notificationServiceClient)
        val data = mapOf("key" to "value")
        sut.put("1337", data)

        verify { notificationServiceClient.post("1337", data) }
    }
}
