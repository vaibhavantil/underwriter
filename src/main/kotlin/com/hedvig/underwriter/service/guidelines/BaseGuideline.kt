package com.hedvig.underwriter.service.guidelines

interface BaseGuideline<T> {

    val guidelineBreached: GuidelineBreached
    val validate: (T) -> Boolean

    val skipAfter: Boolean
        get() = false

    fun invokeValidate(data: T): GuidelineBreached? {
        if (validate.invoke(data)) {
            return guidelineBreached
        }
        return null
    }
}

