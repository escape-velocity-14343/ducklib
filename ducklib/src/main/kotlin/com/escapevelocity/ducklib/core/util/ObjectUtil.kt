package com.escapevelocity.ducklib.core.util

/**
 * Gets the hexadecimal hash of an object.
 *
 * The default representation of an object is its class name concatenated with its hash code in hex,
 * so to replicate this behavior you can do
 * ```
 * "${this::class}@${this.b16Hash}"
 * ```
 */
fun Any.b16Hash(): String = (this.hashCode().toLong() and 0xffffffffL).toString(16)