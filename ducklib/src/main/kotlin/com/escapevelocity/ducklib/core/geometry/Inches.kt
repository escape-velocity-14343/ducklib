package com.escapevelocity.ducklib.core.geometry

import com.escapevelocity.ducklib.core.util.ClosedRangeT
import com.escapevelocity.ducklib.core.util.OpenRangeT
import kotlin.math.hypot

/**
 * Represents a spatial length, normally Inches.
 */
@JvmInline
value class Inches(private val v: Double) : Comparable<Inches> {
    /**
     * Converts this value to inches.
     */
    val inches get() = v

    /**
     * Converts this value to feet.
     */
    val ft get() = v / 12

    /**
     * Converts this value to millimeters.
     */
    val mm get() = v * 25.4

    /**
     * Converts this value to centimeters.
     */
    val cm get() = v * 2.54

    /**
     * Converts this value to meters.
     */
    val m get() = v * 0.254

    operator fun plus(right: Inches) = Inches(this.v + right.inches)
    operator fun minus(right: Inches) = Inches(this.v - right.inches)
    operator fun times(right: Inches) = Inches(this.v * right.inches)
    operator fun div(right: Inches) = Inches(this.v / right.inches)

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

    override fun toString() = "$v\""
    override fun compareTo(other: Inches): Int = this.v.compareTo(other.v)

    operator fun rangeTo(other: Inches) = ClosedRangeT(this, other)
    operator fun rangeUntil(other: Inches) = OpenRangeT(this, other)

    companion object {
        @Deprecated("", ReplaceWith("mm.mm")) fun fromMm(mm: Double) = (mm / 25.4).inches
        @Deprecated("", ReplaceWith("cm.cm")) fun fromCm(cm: Double) = (cm / 2.54).inches
        @Deprecated("", ReplaceWith("m.m")) fun fromM(m: Double) = (m / 0.254).inches
        @Deprecated("", ReplaceWith("ft.ft")) fun fromFt(ft: Double) = (ft * 12.0).inches
        @Deprecated("", ReplaceWith("mm.mm")) fun fromMm(mm: Float) = (mm / 25.4).inches
        @Deprecated("", ReplaceWith("cm.cm")) fun fromCm(cm: Float) = (cm / 2.54).inches
        @Deprecated("", ReplaceWith("m.m")) fun fromM(m: Float) = (m / 0.254).inches
        @Deprecated("", ReplaceWith("ft.ft")) fun fromFt(ft: Float) = (ft / 12.0).inches
    }
}

/**
 * Converts a [Number] in inches to [Inches].
 *
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
inline val Number.inches: Inches
    get() = Inches(this.toDouble())

/**
 * Converts a [Number] in feet to [Inches].
 *
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
inline val Number.ft: Inches
    get() = (toDouble() * 12.0).inches

/**
 * Converts a [Number] in millimeters to [Inches].
 *
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
inline val Number.mm: Inches
    get() = (toDouble() / 25.4).inches

/**
 * Converts a [Number] in cm to [Inches].
 *
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
inline val Number.cm: Inches
    get() = (toDouble() / 2.54).inches

/**
 * Converts a [Number] in meters to [Inches].
 *
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
inline val Number.m: Inches
    get() = (toDouble() / 0.0254).inches

val (() -> Number).inchesSupplier
    get() = { this().inches }

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.plus(right: Inches) = Inches(this.toDouble() + right.inches)

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.minus(right: Inches) = Inches(this.toDouble() - right.inches)

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.times(right: Inches) = Inches(this.toDouble() * right.inches)

/**
 * **NOTE**:
 * This converts the number to a double!
 * If loss of precision occurs, it is NOT MY FAULT.
 */
operator fun Number.div(right: Inches) = Inches(this.toDouble() / right.inches)

fun hypot(x: Inches, y: Inches) = Inches(hypot(x.inches, y.inches))

/**
 * @see kotlin.math.floor
 */
fun floor(value: Inches) = kotlin.math.floor(value.inches).inches

/**
 * @see kotlin.math.ceil
 */
fun ceil(value: Inches) = kotlin.math.ceil(value.inches).inches

/**
 * @see kotlin.math.round
 */
fun round(value: Inches) = kotlin.math.round(value.inches).inches

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