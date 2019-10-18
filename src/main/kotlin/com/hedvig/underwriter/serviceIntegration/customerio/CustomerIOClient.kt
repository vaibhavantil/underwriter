package com.hedvig.underwriter.serviceIntegration.customerio

import feign.auth.BasicAuthRequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "customer.io.client",
    url = "\${customerio.url:https://track.customer.io/api}",
    configuration = [FeignConfiguration::class]
)
interface CustomerIOClient {
    @PutMapping("/v1/customers/{id}")
    fun put(@PathVariable id: String, @RequestBody data: Any): ResponseEntity<String>
}

class FeignConfiguration(
    @Value("\${customerio.username}") val username: String,
    @Value("\${customerio.password}") val password: String
) {
    @Bean
    fun basicAuthRequestInterceptor() = BasicAuthRequestInterceptor(username, password)
}