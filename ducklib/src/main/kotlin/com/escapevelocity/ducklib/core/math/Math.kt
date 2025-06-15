package com.escapevelocity.ducklib.core.math

import com.escapevelocity.ducklib.core.util.ClosedRangeT
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sign

/**
 * Calculates the good modulo of two numbers.
 *
 * The result will always have the same sign as the divisor,
 * instead of the default remainder operators which always have the same sign as the dividend
 *
 * @param dividend The dividend
 * @param divisor The divisor
 * @return The result,
 * which is always in the range `[0, divisor)`.
 */
fun umod(dividend: Byte, divisor: Byte) = ((dividend % divisor) + divisor) % divisor

/**
 * Calculates the good modulo of two numbers.
 *
 * The result will always have the same sign as the divisor,
 * instead of the default remainder operators which always have the same sign as the dividend
 *
 * @param dividend The dividend
 * @param divisor The divisor
 * @return The result,
 * which is always in the range `[0, divisor)`.
 */
fun umod(dividend: Short, divisor: Short) = ((dividend % divisor) + divisor) % divisor

/**
 * Calculates the good modulo of two numbers.
 *
 * The result will always have the same sign as the divisor,
 * instead of the default remainder operators which always have the same sign as the dividend
 *
 * @param dividend The dividend
 * @param divisor The divisor
 * @return The result,
 * which is always in the range `[0, divisor)`.
 */
fun umod(dividend: Int, divisor: Int) = ((dividend % divisor) + divisor) % divisor

/**
 * Calculates the good modulo of two numbers.
 *
 * The result will always have the same sign as the divisor,
 * instead of the default remainder operators which always have the same sign as the dividend
 *
 * @param dividend The dividend
 * @param divisor The divisor
 * @return The result,
 * which is always in the range `[0, divisor)`.
 */
fun umod(dividend: Long, divisor: Long) = ((dividend % divisor) + divisor) % divisor

/**
 * Calculates the good modulo of two numbers.
 *
 * The result will always have the same sign as the divisor,
 * instead of the default remainder operators which always have the same sign as the dividend
 *
 * @param dividend The dividend
 * @param divisor The divisor
 * @return The result,
 * which is always in the range `[0, divisor)`.
 */
fun umod(dividend: Float, divisor: Float) = ((dividend % divisor) + divisor) % divisor

/**
 * Calculates the good modulo of two numbers.
 *
 * The result will always have the same sign as the divisor,
 * instead of the default remainder operators which always have the same sign as the dividend
 *
 * @param dividend The dividend
 * @param divisor The divisor
 * @return The result,
 * which is always in the range `[0, divisor)`.
 */
fun umod(dividend: Double, divisor: Double) = ((dividend % divisor) + divisor) % divisor

/**
 * Rounds [value] down to the nearest [increment]
 * @see ceil
 * @see round
 */
fun floor(value: Double, increment: Double) = floor(value / increment) * increment

/**
 * Rounds [value] up to the nearest [increment]
 * @see floor
 * @see round
 */
fun ceil(value: Double, increment: Double) = ceil(value / increment) * increment

/**
 * Rounds [value] up or down to the nearest [increment]
 * @see floor
 * @see ceil
 */
fun round(value: Double, increment: Double) = round(value / increment) * increment

fun Double.signedPow(exponent: Double) = this.absoluteValue.pow(exponent) * this.sign

/**
 * Interpolates between [min] and [max] by [t].
 *
 * **NOTE**: This function extends nicely to values of [t] outside of [0, 1].
 *
 * @param min The value the function will return when [t] is 0
 * @param max The value the function will return when [t] is 1
 */
fun lerp(min: Double, max: Double, t: Double) = min + (max - min) * t

fun lerp(range: ClosedRange<Double>, t: Double) = lerp(range.start, range.endInclusive, t)

/**
 * The inverse of [lerp] as the name suggests.
 * Maps [t] at to [min] at 0 and to [max] at 1.
 *
 * **NOTE**: This function extends nicely to values of [t] outside of [[min], [max]].
 *
 * @param min The minimum value of [t]
 * @param max The maximum value of [t]
 * @return A value between 0 and 1, as long as [t] is between [min] and [max].
 */
fun invLerp(min: Double, max: Double, t: Double) = (t - min) / (max - min)

fun invLerp(range: ClosedRange<Double>, t: Double) = invLerp(range.start, range.endInclusive, t)

/**
 * Remaps a value [t] in the range [[min0], [max0]] to [[min1], [max1]].
 */
fun remap(min0: Double, max0: Double, min1: Double, max1: Double, t: Double) = lerp(min1, max1, invLerp(min0, max0, t))

fun remap(range0: ClosedRange<Double>, range1: ClosedRange<Double>, t: Double) =
    remap(range0.start, range0.endInclusive, range1.start, range1.endInclusive, t)