package com.hedvig.underwriter.web

import com.hedvig.libs.logging.calls.LogCall
import com.hedvig.underwriter.service.GdprService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/_/v1/gdpr")
class GdprController(
    val gdprService: GdprService
) {
    @PostMapping("/clean")
    @LogCall
    fun clean(): ResponseEntity<out Any> {

        gdprService.clean()

        return ResponseEntity.noContent().build()
    }
}
