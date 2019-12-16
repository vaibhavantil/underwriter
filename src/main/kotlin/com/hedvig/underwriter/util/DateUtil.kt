package com.hedvig.underwriter.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

private val STOCKHOLM_ZONE_ID: ZoneId = ZoneId.of("Europe/Stockholm")!!

fun Instant.toLocalDate() =
    LocalDateTime.ofInstant(this, STOCKHOLM_ZONE_ID).toLocalDate()

fun LocalDateTime.toStockholmInstant(): Instant {
    return this.atZone(STOCKHOLM_ZONE_ID).toInstant()
}
