package com.hedvig.underwriter.service.guidelines

interface BaseGuideline<T> {

    val errorMessage: String
    val validate: (T) -> Boolean

    val skipAfter: Boolean
        get() = false

    fun invokeValidate(data: T): String? {
        if (validate.invoke(data)) {
            return errorMessage
        }
        return null
    }
}
