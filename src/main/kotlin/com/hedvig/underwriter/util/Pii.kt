package com.hedvig.underwriter.util

import java.lang.reflect.Modifier
import java.util.LinkedList

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Pii()

fun Any?.toNonPiiString(): String =
    if (this == null) "null" else reflectionToString(this)

fun Collection<*>?.toNonPiiString(): String =
    this?.joinToString(", ", "[", "]") { it.toNonPiiString() } ?: "null"

fun Map<*, *>?.toNonPiiString(): String =
    this?.map { (key, value) -> "$key=${value.toNonPiiString()}" }?.joinToString(", ", "{", "}") ?: "null"

private fun reflectionToString(obj: Any): String {
    val s = LinkedList<String>()
    var clazz: Class<in Any>? = obj.javaClass

    if (obj::class.java.packageName.startsWith("java"))
        return obj.toString()

    while (clazz != null) {
        for (prop in clazz.declaredFields.filterNot { Modifier.isStatic(it.modifiers) }) {

            prop.isAccessible = true

            val pii = prop.getAnnotation(Pii::class.java)
            val value = if (pii != null) "***" else prop.get(obj)?.toNonPiiString()?.trim()

            s += "${prop.name}=" + value
        }
        clazz = clazz.superclass
    }
    return "${obj.javaClass.simpleName}(${s.joinToString(", ")})"
}
