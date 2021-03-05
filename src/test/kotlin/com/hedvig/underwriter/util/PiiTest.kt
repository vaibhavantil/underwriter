package com.hedvig.underwriter.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.math.BigDecimal

class PiiTest {

    @Test
    fun testPlainPojo() {
        data class Data(
            val a: String,
            val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        val o = Data("sdf", 123, true, 12.345, null)

        assertThat(o.toNonPiiString()).isEqualTo(o.toString())
    }

    @Test
    fun testPojoList() {
        data class Data(
            val a: String,
            val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        val o = listOf(Data("sdf", 123, true, 12.345, null), Data("asfd", 54, true, 7.345, "a"))

        assertThat(o.toNonPiiString()).isEqualTo(o.toString())
    }

    @Test
    fun testPojoSet() {
        data class Data(
            val a: String,
            val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        val o = setOf(Data("sdf", 123, true, 12.345, null), Data("asfd", 54, true, 7.345, "a"))

        assertThat(o.toNonPiiString()).isEqualTo(o.toString())
    }

    @Test
    fun testPojoMap() {
        data class Data(
            val a: String,
            val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        val o = mapOf(1 to Data("sdf", 123, true, 12.345, null), 3 to Data("asfd", 54, true, 7.345, "a"))

        assertThat(o.toNonPiiString()).isEqualTo(o.toString())
    }

    @Test
    fun testNestedPojo() {
        data class NestedData(
            val a: String,
            val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        data class Data(
            val a: String,
            val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?,
            val f: NestedData
        )

        val o = Data("sdf", 123, true, 12.345, null, NestedData("asdds", 134, false, 34.121, "asda"))

        assertThat(o.toNonPiiString()).isEqualTo(o.toString())
    }

    @Test
    fun testPlainPojoWithPii() {
        data class Data(
            @Pii val a: String,
            @Pii val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        val o = Data("sdf", 123, true, 12.345, null)

        assertThat(o.toNonPiiString()).isEqualTo("Data(a=***, b=***, c=true, d=12.345, e=null)")
    }

    @Test
    fun testNestedPojoWithPii() {
        data class NestedData(
            @Pii val a: String,
            @Pii val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        data class Data(
            val a: String,
            val b: Int,
            @Pii val c: Boolean,
            @Pii val d: Double,
            @Pii val e: String?,
            val f: NestedData
        )

        val o = Data("sdf", 123, true, 12.345, null, NestedData("asdds", 134, false, 34.121, "asda"))

        assertThat(o.toNonPiiString()).isEqualTo("Data(a=sdf, b=123, c=***, d=***, e=***, f=NestedData(a=***, b=***, c=false, d=34.121, e=asda))")
    }

    @Test
    fun testMapWithNestedPojoWithPii() {
        data class NestedData(
            @Pii val a: String,
            @Pii val b: Int,
            val c: Boolean,
            val d: Double,
            val e: String?
        )

        data class Data(
            val a: String,
            val b: Int,
            @Pii val c: Boolean,
            @Pii val d: Double,
            @Pii val e: String?,
            val f: NestedData
        )

        val o = mapOf(1 to Data("sdf", 123, true, 12.345, null, NestedData("asdds", 134, false, 34.121, "asda")), 2 to null)

        assertThat(o.toNonPiiString()).isEqualTo("{1=Data(a=sdf, b=123, c=***, d=***, e=***, f=NestedData(a=***, b=***, c=false, d=34.121, e=asda)), 2=null}")
    }

    @Test
    fun testNull() {

        val a: String? = null

        assertThat(a.toNonPiiString()).isEqualTo("null")
    }

    @Test
    fun testKotlinBuildIns() {

        val a = "dafadf"
        assertThat(a.toNonPiiString()).isEqualTo(a)

        val b = 123.341
        assertThat(b.toNonPiiString()).isEqualTo(b.toString())

        val c = false
        assertThat(c.toNonPiiString()).isEqualTo(c.toString())

        val d = BigDecimal(12)
        assertThat(d.toNonPiiString()).isEqualTo(d.toString())
    }
}
