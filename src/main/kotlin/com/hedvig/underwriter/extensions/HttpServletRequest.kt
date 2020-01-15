package com.hedvig.underwriter.extensions

import javax.servlet.http.HttpServletRequest

fun HttpServletRequest.isIOS(): Boolean {
    return getHeader("User-Agent")?.contains(iOSAppUserAgentRegex) ?: false
}

fun HttpServletRequest.isAndroid(): Boolean {
    return getHeader("User-Agent")?.contains(androidAppUserAgentRegex) ?: false
}

private val iOSAppUserAgentRegex = Regex("^com\\.hedvig.+iOS")
private val androidAppUserAgentRegex = Regex("^com\\.hedvig.+Android")
