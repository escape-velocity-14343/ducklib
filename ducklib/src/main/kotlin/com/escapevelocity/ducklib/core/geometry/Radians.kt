package com.escapevelocity.ducklib.core.geometry

import com.escapevelocity.ducklib.core.math.umod
import com.escapevelocity.ducklib.core.util.ClosedRangeT
import com.escapevelocity.ducklib.core.util.OpenRangeT
import java.util.*

@JvmInline
value class Radians(val v: Double) : Comparable<Radians>, Formattable {
    val degrees: Double
        get() = v * 180.0 / kotlin.math.PI

    /**
     * Returns the equivalent angle normalized in the range `[-PI, PI)`.
     */
    val normalized: Radians
        get() = umod(v - PI, TAU) - PI

    /**
     * Adds the two angles together, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to add to this
     */
    operator fun plus(right: Radians) = Radians(this.v + right.v)

    /**
     * Subtracts [right] from this angle, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to subtract from this
     */
    operator fun minus(right: Radians) = Radians(this.v - right.v)

    /**
     * Multiplies this angle by [right].
     * Kind of cursed, if you're using this, something has probably gone wrong
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to multiply this by
     */
    operator fun times(right: Radians) = Radians(this.v * right.v)

    /**
     * Divides this angle by [right].
     * Kind of cursed, if you're using this, something has probably gone wrong
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to divide this by
     */
    operator fun div(right: Radians) = Radians(this.v / right.v)

    /**
     * Calculates the remainder of `this / right`.
     *
     * **NOTE**:
     * This behaves like the rest of the remainder operations,
     * so the sign of the result will always match the sign of this!
     * If you want the "good" behavior use [umod]
     *
     * @param right The angle to rotate this by
     */
    operator fun rem(right: Radians) = Radians(this.v % right.v)

    /**
     * Adds the two angles together, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to add to this
     */
    operator fun plus(right: Double) = Radians(this.v + right)

    /**
     * Subtracts [right] from this angle, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to subtract from this
     */
    operator fun minus(right: Double) = Radians(this.v - right)

    /**
     * Multiplies this angle by [right].
     * Kind of cursed, if you're using this, something has probably gone wrong
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to multiply this by
     */
    operator fun times(right: Double) = Radians(this.v * right)

    /**
     * Divides this angle by [right].
     * Kind of cursed, if you're using this, something has probably gone wrong
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to divide this by
     */
    operator fun div(right: Double) = Radians(this.v / right)

    /**
     * Calculates the remainder of `this / right`.
     *
     * **NOTE**:
     * This behaves like the rest of the remainder operations,
     * so the sign of the result will always match the sign of this!
     * If you want the "good" behavior use [umod]
     *
     * @param right The angle to rotate this by
     */
    operator fun rem(right: Double) = Radians(this.v % right)

    operator fun unaryMinus() = Radians(-this.v)
    operator fun inc() = Radians(this.v + 1)
    operator fun dec() = Radians(this.v - 1)

    operator fun rangeTo(other: Radians) = ClosedRangeT(this, other)
    operator fun rangeUntil(other: Radians) = OpenRangeT(this, other)

    /**
     * Rotate this by [x] and returns the result as a normalized angle
     *
     * @param x The rotation to apply to this
     */
    fun rotated(x: Radians) = (this + x).normalized

    /**
     * Calculate the **signed** angular difference between this and [other], taking into account angle wrapping
     */
    fun angleTo(other: Radians) = (other - this).normalized

    override fun toString() = "%.3s".format(this)
    override fun compareTo(other: Radians): Int = this.v.compareTo(other.v)
    override fun formatTo(
        formatter: Formatter?,
        flags: Int,
        width: Int,
        precision: Int
    ) {
        formatter?.format("%.${precision}f (%.${precision}f°)", v, degrees)
    }

    companion object {
        val ZERO = 0.0.radians

        /**
         * Half-pi constant in [com.escapevelocity.ducklib.core.geometry.Radians]
         */
        val HPI = kotlin.math.PI.radians * 0.5

        /**
         * Pi constant in [com.escapevelocity.ducklib.core.geometry.Radians]
         */
        val PI = kotlin.math.PI.radians

        /**
         * Tau (2pi) constant in [com.escapevelocity.ducklib.core.geometry.Radians]
         */
        val TAU = kotlin.math.PI.radians * 2.0

        fun fromDegrees(degrees: Double) = PI / 180.0 * degrees
        fun fromRotations(rotations: Double) = rotations * TAU
    }
}

/**
 * Convenience property to wrap a Double in [Radians].
 *
 * This doesn't normalize the angle, if you want that use [Radians.normalized] separately
 */
inline val Double.radians
    get() = Radians(this)
inline val (() -> Double).radiansSupplier
    get() = { this().radians }

/**
 * @see [Radians.plus]
 */
operator fun Double.plus(right: Radians) = Radians(this + right.v)

/**
 * @see [Radians.minus]
 */
operator fun Double.minus(right: Radians) = Radians(this - right.v)

/**
 * @see [Radians.times]
 */
operator fun Double.times(right: Radians) = Radians(this * right.v)

/**
 * @see [Radians.div]
 */
operator fun Double.div(right: Radians) = Radians(this / right.v)

/**
 * @see [Radians.rem]
 */
operator fun Double.rem(right: Radians) = Radians(this % right.v)

/**
 * @see [kotlin.math.cos]
 */
fun cos(x: Radians) = kotlin.math.cos(x.v)

/**
 * @see [kotlin.math.sin]
 */
fun sin(x: Radians) = kotlin.math.sin(x.v)

/**
 * @see [kotlin.math.tan]
 */
fun tan(x: Radians) = kotlin.math.tan(x.v)

/**
 * @see [kotlin.math.acos]
 */
fun acos(x: Double) = kotlin.math.acos(x).radians

/**
 * @see [kotlin.math.asin]
 */
fun asin(x: Double) = kotlin.math.asin(x).radians

/**
 * @see [kotlin.math.atan]
 */
fun atan(x: Double) = kotlin.math.atan(x).radians

/**
 * @see com.escapevelocity.ducklib.core.math.umod
 */
fun umod(x: Radians, y: Radians) = Radians(umod(x.v, y.v))
fun atan2(y: Inches, x: Inches) = kotlin.math.atan2(y.v, x.v).radians

/**
 * @see kotlin.math.floor
 */
fun floor(value: Radians) = kotlin.math.floor(value.v).radians

/**
 * @see kotlin.math.ceil
 */
fun ceil(value: Radians) = kotlin.math.ceil(value.v).radians

/**
 * @see kotlin.math.round
 */
fun round(value: Radians) = kotlin.math.round(value.v).radians

/**
 * Rounds [value] down to the nearest [increment]
 * @see ceil
 * @see round
 */
fun floor(value: Radians, increment: Radians) = floor(value / increment) * increment

/**
 * Rounds [value] up to the nearest [increment]
 * @see floor
 * @see round
 */
fun ceil(value: Radians, increment: Radians) = ceil(value / increment) * increment

/**
 * Rounds [value] up or down to the nearest [increment]
 * @see floor
 * @see ceil
 */
fun round(value: Radians, increment: Radians) = round(value / increment) * increment