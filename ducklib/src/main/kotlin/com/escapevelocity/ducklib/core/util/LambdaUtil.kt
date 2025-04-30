package com.escapevelocity.ducklib.core.util

operator fun <T : () -> Boolean> T.not() = { !this() }
infix fun <T : () -> Boolean> T.and(right: () -> Boolean) = { this() and right() }
infix fun <T : () -> Boolean> T.or(right: () -> Boolean) = { this() or right() }
infix fun <T : () -> Boolean> T.xor(right: () -> Boolean) = { this() xor right() }