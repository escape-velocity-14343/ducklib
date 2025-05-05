package com.escapevelocity.ducklib.core.math

import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.round

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