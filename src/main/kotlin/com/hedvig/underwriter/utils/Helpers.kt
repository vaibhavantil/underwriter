package com.hedvig.underwriter.utils

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Helpers {
    val logger: Logger
        get() = LoggerFactory.getLogger("Logger")
}