package com.escapevelocity.ducklib.core.geometry

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

data class Vector2(val x: Inches, val y: Inches) {
    override fun toString() = "($x, $y)"

    val angle: Radians
        get() = atan2(y, x)
    val length: Inches
        get() = hypot(x, y)
    val lengthSquared: Double
        get() = (x * x + y * y)
    val normalized
        get() = this / length
    val yx
        get() = Vector2(y, x)
    val xx
        get() = Vector2(x, x)
    val yy
        get() = Vector2(y, y)

    fun rotated(t: Radians) = Vector2(x * cos(t) - y * sin(t), y * cos(t) + x * sin(t))
    fun distanceTo(other: Vector2) = (other - this).length
    fun distanceSquaredTo(other: Vector2) = (other - this).lengthSquared
    fun angleTo(other: Vector2) = (other - this).angle
    fun setLength(length: Inches) = this.normalized * length
    fun limitLength(length: Inches) = if (this.length > length) this.setLength(length) else this

    operator fun plus(right: Vector2) = Vector2(x + right.x, y + right.y)
    operator fun minus(right: Vector2) = Vector2(x - right.x, y - right.y)
    operator fun times(right: Vector2) = Vector2(x * right.x, y * right.y)
    operator fun times(right: Inches) = Vector2(x * right, y * right)
    operator fun div(right: Vector2) = Vector2(x / right.x, y / right.y)
    operator fun div(right: Inches) = Vector2(x / right, y / right)
    infix fun dot(right: Vector2) = x * right.x + y * right.y

    operator fun get(index: Int): Inches = when (index) {
        0 -> x
        1 -> y
        else -> throw IndexOutOfBoundsException("Index $index is not a valid axis index for Vector2")
    }

    companion object Factory {
        fun fromAngle(angle: Radians, length: Inches = 1.0) = Vector2(cos(angle) * length, sin(angle) * length)
    }
}