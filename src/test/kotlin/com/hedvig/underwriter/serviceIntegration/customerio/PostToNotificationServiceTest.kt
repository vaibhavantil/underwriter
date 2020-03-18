package com.hedvig.underwriter.serviceIntegration.customerio

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Test
import org.junit.Before
import org.springframework.http.ResponseEntity

import com.hedvig.underwriter.serviceIntegration.notificationService.NotificationServiceClient as IntegrationClient

class PostToNotificationServiceTest {

    @MockK
    lateinit var notificationServiceClient: IntegrationClient

    @Before
    fun setup(){
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
