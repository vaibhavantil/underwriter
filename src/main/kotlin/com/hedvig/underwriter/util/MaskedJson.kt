package com.hedvig.underwriter.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.hedvig.libs.logging.masking.Masked
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair

fun Any?.toMaskedJsonString(objectMapper: ObjectMapper = ObjectMapper()): String {
    this ?: return ""

    val mapper = objectMapper.copy()

    val default = mapper.serializationConfig.annotationIntrospector
    val introspector = AnnotationIntrospectorPair.pair(default, MaskedIntrospector())

    mapper.setAnnotationIntrospectors(introspector, null)

    return mapper.writeValueAsString(this)
}

private class MaskedIntrospector : NopAnnotationIntrospector() {
    override fun findSerializer(method: Annotated): Any? {
        method.getAnnotation(Masked::class.java) ?: return null

        return MaskedSerializer::class.java
    }
}

private class MaskedSerializer : StdSerializer<Any>(Any::class.java) {
    override fun serialize(value: Any, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeNull()
    }
}
