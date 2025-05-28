package com.escapevelocity.ducklib.core.util

/**
 * Inverts a boolean supplier.
 */
operator fun <T : () -> Boolean> T.not() = { !this() }

/**
 * Composes two boolean suppliers with [kotlin.Boolean.and] logic.
 *
 * **NOTE**:
 * Both suppliers will be evaluated!
 * This *does not* short-circuit!
 */
infix fun <T : () -> Boolean> T.and(right: () -> Boolean) = { this() and right() }

/**
 * Composes two boolean suppliers with [kotlin.Boolean.or] logic.
 *
 * **NOTE**:
 * Both suppliers will be evaluated!
 * This *does not* short-circuit!
 */
infix fun <T : () -> Boolean> T.or(right: () -> Boolean) = { this() or right() }

/**
 * Composes two boolean suppliers with [kotlin.Boolean.xor] logic.
 */
infix fun <T : () -> Boolean> T.xor(right: () -> Boolean) = { this() xor right() }
