package com.hedvig.underwriter.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

private val STOCKHOLM_ZONE_ID: ZoneId = ZoneId.of("Europe/Stockholm")!!

fun Instant.toStockholmLocalDate() =
    LocalDate.ofInstant(this, STOCKHOLM_ZONE_ID)

fun LocalDateTime.toStockholmInstant(): Instant {
    return this.atZone(STOCKHOLM_ZONE_ID).toInstant()
}
