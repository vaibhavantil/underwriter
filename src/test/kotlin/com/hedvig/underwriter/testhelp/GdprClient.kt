package com.hedvig.underwriter.testhelp

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component

@Component
class GdprClient {

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    fun clean(dryRun: Boolean = false): ResponseEntity<String> {
        return restTemplate.postForEntity("/_/v1/gdpr/clean${if (dryRun) "?dry-run=true" else ""}")
    }
}
