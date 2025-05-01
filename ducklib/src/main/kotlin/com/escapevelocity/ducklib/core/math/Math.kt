package com.escapevelocity.ducklib.core.math

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