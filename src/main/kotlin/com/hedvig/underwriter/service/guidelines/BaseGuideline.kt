package com.hedvig.underwriter.service.guidelines

interface BaseGuideline<T> {

    val breachedGuideline: BreachedGuideline
    val validate: (T) -> Boolean

    val skipAfter: Boolean
        get() = false

    fun invokeValidate(data: T): BreachedGuideline? {
        if (validate.invoke(data)) {
            return breachedGuideline
        }
        return null
    }
}

