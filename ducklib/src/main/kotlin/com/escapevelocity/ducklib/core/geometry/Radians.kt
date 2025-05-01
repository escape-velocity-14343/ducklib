package com.escapevelocity.ducklib.core.geometry

import com.escapevelocity.ducklib.core.math.umod
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.TypeHost

//@JvmInline
class Radians(val v: Double) : Comparable<Radians> {
    val degrees: Double
        get() = v * 180.0 / kotlin.math.PI
    val normalized: Radians
        get() = umod(v, 360.0).radians
    operator fun plus(right: Radians) = Radians(this.v + right.v)
    operator fun minus(right: Radians) = Radians(this.v - right.v)
    operator fun times(right: Radians) = Radians(this.v * right.v)
    operator fun div(right: Radians) = Radians(this.v / right.v)
    operator fun rem(right: Radians) = Radians(this.v % right.v)

    operator fun plus(right: Number) = Radians(this.v + right.toDouble())
    operator fun minus(right: Number) = Radians(this.v - right.toDouble())
    operator fun times(right: Number) = Radians(this.v * right.toDouble())
    operator fun div(right: Number) = Radians(this.v / right.toDouble())
    operator fun rem(right: Number) = Radians(this.v % right.toDouble())

    operator fun unaryMinus() = Radians(-this.v)
    operator fun inc() = Radians(this.v + 1)
    operator fun dec() = Radians(this.v - 1)

    fun rotated(x: Radians) = (this + x).normalized

    override fun toString() = v.toString()
    override fun compareTo(other: Radians): Int = this.v.compareTo(other.v)

    companion object Factory {
        fun fromDegrees(degrees: Double) = (kotlin.math.PI / 180.0 * degrees).radians
    }
}

val Number.radians: Radians
    get() = Radians(this.toDouble())

operator fun Number.plus(right: Radians) = Radians(this.toDouble() + right.v)
operator fun Number.minus(right: Radians) = Radians(this.toDouble() - right.v)
operator fun Number.times(right: Radians) = Radians(this.toDouble() * right.v)
operator fun Number.div(right: Radians) = Radians(this.toDouble() / right.v)

fun cos(x: Radians) = kotlin.math.cos(x.v)
fun sin(x: Radians) = kotlin.math.sin(x.v)
fun tan(x: Radians) = kotlin.math.tan(x.v)

fun acos(x: Double) = kotlin.math.acos(x).radians
fun asin(x: Double) = kotlin.math.asin(x).radians
fun atan(x: Double) = kotlin.math.atan(x).radians
fun distance(x: Radians, y: Radians) = (y - x).normalized
fun atan2(y: Inches, x: Inches) = kotlin.math.atan2(y.v, x.v).radians