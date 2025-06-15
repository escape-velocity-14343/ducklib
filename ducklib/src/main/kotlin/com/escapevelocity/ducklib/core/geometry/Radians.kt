package com.escapevelocity.ducklib.core.geometry

import com.escapevelocity.ducklib.core.geometry.Radians.Companion.PI
import com.escapevelocity.ducklib.core.geometry.Radians.Companion.TAU
import com.escapevelocity.ducklib.core.math.umod
import com.escapevelocity.ducklib.core.util.ClosedRangeT
import com.escapevelocity.ducklib.core.util.OpenRangeT
import java.util.*

/**
 * Represents an angle or arc length in radians, where one full rotation is 2π radians.
 */
@JvmInline
value class Radians(val radians: Double) : Comparable<Radians>, Formattable {
    val degrees
        get() = radians * 180.0 / kotlin.math.PI
    val rotations
        get() = radians / TAU.radians

    /**
     * Returns the equivalent angle normalized in the range [-[PI], [PI]).
     */
    val normalized
        get() = umod(radians - PI, TAU) - PI

    /**
     * Adds the two angles together, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to add to this
     */
    operator fun plus(right: Radians) = Radians(this.radians + right.radians)

    /**
     * Subtracts [right] from this angle, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to subtract from this
     */
    operator fun minus(right: Radians) = Radians(this.radians - right.radians)

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
    operator fun times(right: Radians) = Radians(this.radians * right.radians)

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
    operator fun div(right: Radians) = Radians(this.radians / right.radians)

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
    operator fun rem(right: Radians) = Radians(this.radians % right.radians)

    /**
     * Adds the two angles together, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to add to this
     */
    operator fun plus(right: Double) = Radians(this.radians + right)

    /**
     * Subtracts [right] from this angle, like a rotation that **doesn't wrap**.
     *
     * **NOTE**:
     * Does *not* include normalization logic!
     * Use [rotated] if you want that.
     *
     * @param right The angle to subtract from this
     */
    operator fun minus(right: Double) = Radians(this.radians - right)

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
    operator fun times(right: Double) = Radians(this.radians * right)

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
    operator fun div(right: Double) = Radians(this.radians / right)

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
    operator fun rem(right: Double) = Radians(this.radians % right)

    operator fun unaryMinus() = Radians(-this.radians)
    operator fun inc() = Radians(this.radians + 1)
    operator fun dec() = Radians(this.radians - 1)

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
    override fun compareTo(other: Radians): Int = this.radians.compareTo(other.radians)
    override fun formatTo(
        formatter: Formatter?,
        flags: Int,
        width: Int,
        precision: Int
    ) {
        formatter?.format("%.${precision}f (%.${precision}f°)", radians, degrees)
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
    }
}

/**
 * Convenience property to wrap a Double in [Radians].
 *
 * This doesn't normalize the angle, if you want that use [Radians.normalized] separately
 */
inline val Number.radians
    get() = Radians(toDouble())

inline val Number.degrees
    get() = PI / 180.0 * toDouble()

inline val Number.rotations
    get() = TAU * toDouble()

/**
 * Convenience property to wrap a Float in [Radians].
 *
 * This doesn't normalize the angle, if you want that use [Radians.normalized] separately
 */
inline val Float.radians
    get() = Radians(this.toDouble())
inline val (() -> Double).radiansSupplier
    get() = { this().radians }

/**
 * @see [Radians.plus]
 */
operator fun Double.plus(right: Radians) = Radians(this + right.radians)

/**
 * @see [Radians.minus]
 */
operator fun Double.minus(right: Radians) = Radians(this - right.radians)

/**
 * @see [Radians.times]
 */
operator fun Double.times(right: Radians) = Radians(this * right.radians)

/**
 * @see [Radians.div]
 */
operator fun Double.div(right: Radians) = Radians(this / right.radians)

/**
 * @see [Radians.rem]
 */
operator fun Double.rem(right: Radians) = Radians(this % right.radians)

/**
 * @see [kotlin.math.cos]
 */
fun cos(x: Radians) = kotlin.math.cos(x.radians)

/**
 * @see [kotlin.math.sin]
 */
fun sin(x: Radians) = kotlin.math.sin(x.radians)

/**
 * @see [kotlin.math.tan]
 */
fun tan(x: Radians) = kotlin.math.tan(x.radians)

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
fun umod(x: Radians, y: Radians) = Radians(umod(x.radians, y.radians))
fun atan2(y: Inches, x: Inches) = kotlin.math.atan2(y.inches, x.inches).radians

/**
 * @see kotlin.math.floor
 */
fun floor(value: Radians) = kotlin.math.floor(value.radians).radians

/**
 * @see kotlin.math.ceil
 */
fun ceil(value: Radians) = kotlin.math.ceil(value.radians).radians

/**
 * @see kotlin.math.round
 */
fun round(value: Radians) = kotlin.math.round(value.radians).radians

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