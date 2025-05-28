package com.escapevelocity.ducklib.control

class Limiter<T : Comparable<T>>(val minValue: T? = null, val maxValue: T? = null) : (T) -> T {
    override fun invoke(p1: T) = when {
        minValue != null && p1 < minValue -> minValue
        maxValue != null && p1 > maxValue -> maxValue
        else -> p1
    }
}