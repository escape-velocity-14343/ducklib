package com.escapevelocity.ducklib.core.geometry

import com.escapevelocity.ducklib.core.util.ClosedRangeT
import com.escapevelocity.ducklib.core.util.OpenRangeT
import kotlin.math.hypot

@JvmInline
value class Inches(val v: Double) : Comparable<Inches> {
    operator fun plus(right: Inches) = Inches(this.v + right.v)
    operator fun minus(right: Inches) = Inches(this.v - right.v)
    operator fun times(right: Inches) = Inches(this.v * right.v)
    operator fun div(right: Inches) = Inches(this.v / right.v)

    /**
     * **NOTE**:
     * This converts the number to a double!
     * If loss of precision occurs, it is NOT MY FAULT.
     */
    operator fun plus(right: Number) = Inches(this.v + right.toDouble())

    /**
     * **NOTE**:
     * This converts the number to a double!
     * If loss of precision occurs, it is NOT MY FAULT.
     */
    operator fun minus(right: Number) = Inches(this.v - right.toDouble())

    /**
     * **NOTE**:
     * This converts the number to a double!
     * If loss of precision occurs, it is NOT MY FAULT.
     */
    operator fun times(right: Number) = Inches(this.v * right.toDouble())

    /**
     * **NOTE**:
     * This converts the number to a double!
     * If loss of precision occurs, it is NOT MY FAULT.
     */
    operator fun div(right: Number) = Inches(this.v / right.toDouble())

    operator fun unaryPlus() = this
    operator fun unaryMinus() = Inches(-this.v)
    operator fun inc() = Inches(this.v + 1)
    operator fun dec() = Inches(this.v - 1)

    override fun toString() = v.toString()
    override fun compareTo(other: Inches): Int = this.v.compareTo(other.v)

    operator fun rangeTo(other: Inches) = ClosedRangeT(this, other)
    operator fun rangeUntil(other: Inches) = OpenRangeT(this, other)

    companion object {
        fun fromMm(mm: Double) = (mm / 25.4).inches
        fun fromCm(cm: Double) = (cm / 2.54).inches
        fun fromM(m: Double) = (m / 0.254).inches
        fun fromFt(ft: Double) = (ft / 12.0).inches
    }
}

/**
 * Converts a [Double] to [Inches].
 */
inline val Double.inches: Inches
    get() = Inches(this)

/**
 * Converts a [Number] to [Inches].
 *
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
inline val Number.inches: Inches
    get() = Inches(this.toDouble())
val (() -> Double).inchesSupplier
    get() = { this().inches }

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.plus(right: Inches) = Inches(this.toDouble() + right.v)

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.minus(right: Inches) = Inches(this.toDouble() - right.v)

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.times(right: Inches) = Inches(this.toDouble() * right.v)

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.div(right: Inches) = Inches(this.toDouble() / right.v)

fun hypot(x: Inches, y: Inches) = Inches(hypot(x.v, y.v))

/**
 * @see kotlin.math.floor
 */
fun floor(value: Inches) = kotlin.math.floor(value.v).inches

/**
 * @see kotlin.math.ceil
 */
fun ceil(value: Inches) = kotlin.math.ceil(value.v).inches

/**
 * @see kotlin.math.round
 */
fun round(value: Inches) = kotlin.math.round(value.v).inches

/**
 * Rounds [value] down to the nearest [increment]
 * @see ceil
 * @see round
 */
fun floor(value: Inches, increment: Inches) = floor(value / increment) * increment

/**
 * Rounds [value] up to the nearest [increment]
 * @see floor
 * @see round
 */
fun ceil(value: Inches, increment: Inches) = ceil(value / increment) * increment

/**
 * Rounds [value] up or down to the nearest [increment]
 * @see floor
 * @see ceil
 */
fun round(value: Inches, increment: Inches) = round(value / increment) * increment