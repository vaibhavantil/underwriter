package com.hedvig.underwriter.service.guidelines

import com.hedvig.underwriter.model.QuoteData
import kotlin.reflect.KClass
import kotlin.reflect.full.cast

interface BaseGuideline<in T : QuoteData> {

    val skipAfter: Boolean
        get() = false

    fun validate(data: T): BreachedGuidelineCode?
}

class TypedGuideline<G : QuoteData, Q : QuoteData>(
    private val guideline: BaseGuideline<Q>,
    private val q: KClass<Q>
) : BaseGuideline<G> {
    override fun validate(data: G): BreachedGuidelineCode? {
        return guideline.validate(q.cast(data))
    }
}
